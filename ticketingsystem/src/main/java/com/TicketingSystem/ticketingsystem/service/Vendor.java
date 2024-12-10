package com.TicketingSystem.ticketingsystem.service;

import com.TicketingSystem.ticketingsystem.controller.WebSocketController;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int releaseRate;
    private int totalTickets;
    private volatile boolean running = true;
    private final WebSocketController webSocketController;
    private final int vendorId;
    private final TicketService ticketService; // Reference to update stats

    public Vendor(TicketPool ticketPool, int releaseRate, int totalTickets, WebSocketController webSocketController, int vendorId, TicketService ticketService) {
        this.ticketPool = ticketPool;
        this.releaseRate = releaseRate;
        this.totalTickets = totalTickets;
        this.webSocketController = webSocketController;
        this.vendorId = vendorId;
        this.ticketService = ticketService;
    }

    @Override
    public void run() {
        while (running && totalTickets > 0) {
            int actualRelease = Math.min(releaseRate, totalTickets);
            ticketPool.addTickets(actualRelease);
            totalTickets -= actualRelease;
            webSocketController.sendTicketUpdate("Vendor " + vendorId + " added " + actualRelease + " tickets. Remaining for this vendor: " + totalTickets);

            // Update vendor stats in ticketService
            ticketService.incrementVendorCount(vendorId, actualRelease);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        running = false;
    }
}
