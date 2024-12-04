// Customer.java

package com.TicketingSystem.ticketingsystem.service;

import com.TicketingSystem.ticketingsystem.controller.WebSocketController;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int retrievalRate;
    private volatile boolean running = true;
    private final WebSocketController webSocketController;

    public Customer(TicketPool ticketPool, int retrievalRate, WebSocketController webSocketController) {
        this.ticketPool = ticketPool;
        this.retrievalRate = retrievalRate;
        this.webSocketController = webSocketController;
    }

    @Override
    public void run() {
        while (running) {
            for (int i = 0; i < retrievalRate; i++) {
                ticketPool.removeTicket();
                webSocketController.sendTicketUpdate("Customer retrieved a ticket.");
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
