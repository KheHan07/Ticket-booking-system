package com.TicketingSystem.ticketingsystem.service;

import com.TicketingSystem.ticketingsystem.controller.WebSocketController;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int releaseRate;
    private int totalTickets;
    private volatile boolean running = true;
    private final WebSocketController webSocketController;

    public Vendor(TicketPool ticketPool, int releaseRate, int totalTickets, WebSocketController webSocketController) {
        this.ticketPool = ticketPool;
        this.releaseRate = releaseRate;
        this.totalTickets = totalTickets;
        this.webSocketController = webSocketController;
    }


    @Override
    public void run() {
        while (running && totalTickets > 0) {
            ticketPool.addTickets(releaseRate);
            totalTickets -= releaseRate;
            webSocketController.sendTicketUpdate("Vendor added " + releaseRate + " tickets. Remaining: " + totalTickets);
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
