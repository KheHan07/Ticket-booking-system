package com.TicketingSystem.ticketingsystem.service;

import com.TicketingSystem.ticketingsystem.dto.TicketConfigDto;
import com.TicketingSystem.ticketingsystem.dto.StartStopDto;
import com.TicketingSystem.ticketingsystem.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TicketService {
    private ExecutorService executorService;
    private boolean running = false;
    private TicketPool ticketPool;
    private Vendor[] vendors;
    private Customer[] customers;

    @Autowired
    private WebSocketController webSocketController;

    // Add fields for the configuration parameters
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;

    public void configure(TicketConfigDto configDto) {
        // Set the configuration values
        this.totalTickets = configDto.getTotalTickets();
        this.ticketReleaseRate = configDto.getTicketReleaseRate();
        this.customerRetrievalRate = configDto.getCustomerRetrievalRate();
        this.ticketPool = new TicketPool(configDto.getMaxTicketCapacity(), webSocketController);

        // Default number of vendors and customers
        vendors = new Vendor[2];
        customers = new Customer[3];

        // Initialize vendors and customers
        for (int i = 0; i < vendors.length; i++) {
            vendors[i] = new Vendor(ticketPool, ticketReleaseRate, totalTickets / vendors.length, webSocketController);
        }
        for (int i = 0; i < customers.length; i++) {
            customers[i] = new Customer(ticketPool, customerRetrievalRate, webSocketController);
        }

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
        // Set the number of vendors and customers based on the new configuration
        int numberOfVendors = dto.getNumberOfVendors();
        int numberOfCustomers = dto.getNumberOfBuyers();

        this.vendors = new Vendor[numberOfVendors];
        this.customers = new Customer[numberOfCustomers];

        // Initialize new vendors
        for (int i = 0; i < numberOfVendors; i++) {
            this.vendors[i] = new Vendor(ticketPool, ticketReleaseRate, totalTickets / numberOfVendors, webSocketController);
        }

        // Initialize new customers
        for (int i = 0; i < numberOfCustomers; i++) {
            this.customers[i] = new Customer(ticketPool, customerRetrievalRate, webSocketController);
        }

        System.out.println("Number of vendors and buyers set successfully.");
    }
}
