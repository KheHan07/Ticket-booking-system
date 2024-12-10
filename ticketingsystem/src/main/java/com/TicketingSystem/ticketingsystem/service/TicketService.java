package com.TicketingSystem.ticketingsystem.service;

import com.TicketingSystem.ticketingsystem.controller.WebSocketController;
import com.TicketingSystem.ticketingsystem.dto.TicketConfigDto;
import com.TicketingSystem.ticketingsystem.dto.StartStopDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class TicketService {
    private ExecutorService executorService;
    private boolean running = false;
    private TicketPool ticketPool;
    private Vendor[] vendors;
    private Customer[] customers;

    @Autowired
    private WebSocketController webSocketController;

    // Configuration parameters
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;

    // Stats
    private int[] vendorTicketCounts; // Tracks tickets released by each vendor
    private int customersTotalBought = 0;
    // totalTicketsSoldOut: This is basically customersTotalBought (since sold out = bought)
    // but we keep a separate variable if needed. Here let's just treat them as the same.
    // If totalTickets refers to total configured tickets at start, sold out can be min of (customersTotalBought, totalTickets)
    // For simplicity, totalTicketsSoldOut = customersTotalBought.
    // They can be the same value.

    public void configure(TicketConfigDto configDto) {
        this.totalTickets = configDto.getTotalTickets();
        this.ticketReleaseRate = configDto.getTicketReleaseRate();
        this.customerRetrievalRate = configDto.getCustomerRetrievalRate();
        this.maxTicketCapacity = configDto.getMaxTicketCapacity();

        this.ticketPool = new TicketPool(maxTicketCapacity, webSocketController);

        // Default vendors and customers count
        // Will be overridden by setVendorsAndBuyers if called after this.
        vendors = new Vendor[2];
        customers = new Customer[3];

        // Initialize vendors and customers with dummy vendor IDs
        for (int i = 0; i < vendors.length; i++) {
            vendors[i] = new Vendor(ticketPool, ticketReleaseRate, totalTickets / vendors.length, webSocketController, i+1, this);
        }
        for (int i = 0; i < customers.length; i++) {
            customers[i] = new Customer(ticketPool, customerRetrievalRate, webSocketController, this);
        }

        // Initialize stats arrays
        vendorTicketCounts = new int[vendors.length];

        System.out.println("Configuration completed with: " + configDto);
    }

    public String startSystem(StartStopDto dto) {
        if (running) {
            return "System is already running";
        }
        running = true;

        executorService = Executors.newCachedThreadPool();

        // Start vendors and customers
        for (Vendor vendor : vendors) {
            executorService.execute(vendor);
        }
        for (Customer customer : customers) {
            executorService.execute(customer);
        }

        return "System started successfully";
    }

    public void stopSystem() {
        if (!running) {
            System.out.println("System is not running");
            return;
        }
        running = false;
        executorService.shutdown();

        // Stop vendors and customers
        for (Vendor vendor : vendors) {
            vendor.stop();
        }
        for (Customer customer : customers) {
            customer.stop();
        }
        System.out.println("Stopping the system");
    }

    public void setVendorsAndBuyers(StartStopDto dto) {
        int numberOfVendors = dto.getNumberOfVendors();
        int numberOfCustomers = dto.getNumberOfBuyers();

        this.vendors = new Vendor[numberOfVendors];
        this.customers = new Customer[numberOfCustomers];

        // Re-initialize stats arrays
        vendorTicketCounts = new int[numberOfVendors];
        customersTotalBought = 0;

        for (int i = 0; i < numberOfVendors; i++) {
            this.vendors[i] = new Vendor(ticketPool, ticketReleaseRate, totalTickets / numberOfVendors, webSocketController, i+1, this);
        }

        for (int i = 0; i < numberOfCustomers; i++) {
            this.customers[i] = new Customer(ticketPool, customerRetrievalRate, webSocketController, this);
        }

        System.out.println("Number of vendors and buyers set successfully.");
    }

    public void resetSystem() {
        // Reset everything
        stopSystem();
        this.totalTickets = 150; // can set defaults or leave as is until reconfigure
        this.ticketReleaseRate = 5;
        this.customerRetrievalRate = 3;
        this.maxTicketCapacity = 100;

        this.ticketPool = new TicketPool(maxTicketCapacity, webSocketController);
        // Default again
        this.vendors = new Vendor[2];
        this.customers = new Customer[3];

        for (int i = 0; i < vendors.length; i++) {
            vendors[i] = new Vendor(ticketPool, ticketReleaseRate, totalTickets / vendors.length, webSocketController, i+1, this);
        }
        for (int i = 0; i < customers.length; i++) {
            customers[i] = new Customer(ticketPool, customerRetrievalRate, webSocketController, this);
        }

        vendorTicketCounts = new int[vendors.length];
        customersTotalBought = 0;

        // Not running yet until we call start again
        System.out.println("System reset successfully.");
    }

    // Stats update methods
    public synchronized void incrementVendorCount(int vendorId, int count) {
        // vendorId starts from 1, array index from 0
        vendorTicketCounts[vendorId - 1] += count;
        sendStatsUpdate();
    }

    public synchronized void incrementCustomerCount(int count) {
        customersTotalBought += count;
        sendStatsUpdate();
    }

    private synchronized void sendStatsUpdate() {
        // Create JSON for stats
        JSONObject json = new JSONObject();
        JSONArray vendorStatsArray = new JSONArray();
        for (int i = 0; i < vendorTicketCounts.length; i++) {
            JSONObject v = new JSONObject();
            v.put("vendorId", i+1);
            v.put("ticketsReleased", vendorTicketCounts[i]);
            vendorStatsArray.put(v);
        }
        json.put("vendorStats", vendorStatsArray);
        json.put("customersTotalBought", customersTotalBought);
        json.put("totalTicketsSoldOut", customersTotalBought);

        // Send JSON string
        webSocketController.sendStatsUpdate(json.toString());
    }
}
