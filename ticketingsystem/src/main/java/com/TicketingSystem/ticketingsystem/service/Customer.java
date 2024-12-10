package com.TicketingSystem.ticketingsystem.service;

import com.TicketingSystem.ticketingsystem.controller.WebSocketController;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int retrievalRate;
    private volatile boolean running = true;
    private final WebSocketController webSocketController;
    private final TicketService ticketService;

    public Customer(TicketPool ticketPool, int retrievalRate, WebSocketController webSocketController, TicketService ticketService) {
        this.ticketPool = ticketPool;
        this.retrievalRate = retrievalRate;
        this.webSocketController = webSocketController;
        this.ticketService = ticketService;
    }

    @Override
    public void run() {
        while (running) {
            for (int i = 0; i < retrievalRate; i++) {
                Integer t = ticketPool.removeTicket();
                if (t != null) {
                    webSocketController.sendTicketUpdate("Customer retrieved a ticket: " + t);
                    // Update customer stats in ticketService
                    ticketService.incrementCustomerCount(1);
                }
            }
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
