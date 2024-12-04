package com.TicketingSystem.ticketingsystem.service;

import com.TicketingSystem.ticketingsystem.controller.WebSocketController;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

public class TicketPool {
    private final List<Integer> tickets;
    private final int capacity;
    private static final String LOG_FILE = "ticket_system.log";
    private final WebSocketController webSocketController;

    public TicketPool(int capacity, WebSocketController webSocketController) {
        this.capacity = capacity;
        this.tickets = Collections.synchronizedList(new ArrayList<>());
        this.webSocketController = webSocketController;
    }

    public synchronized void addTickets(int count) {
        while (tickets.size() + count > capacity) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        for (int i = 0; i < count; i++) {
            tickets.add(i);
            logToFile("Ticket added: " + i);
            webSocketController.sendTicketUpdate("Ticket added: " + i);
            System.out.println("Ticket added: " + i);
        }
        webSocketController.sendTicketUpdate("Tickets available after addition: " + tickets.size());
        System.out.println("Tickets available after addition: " + tickets.size());
        notifyAll();
    }

    public synchronized Integer removeTicket() {
        while (tickets.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        Integer ticket = tickets.remove(0);
        if (ticket != null) {
            logToFile("Ticket removed: " + ticket);
            webSocketController.sendTicketUpdate("Ticket removed: " + ticket);
            System.out.println("Ticket removed: " + ticket);
        }
        webSocketController.sendTicketUpdate("Tickets available after removal: " + tickets.size());
        System.out.println("Tickets available after removal: " + tickets.size());
        notifyAll();
        return ticket;
    }

    public int getAvailableTickets() {
        return tickets.size();
    }

    private void logToFile(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            System.out.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
