package com.TicketingSystem.ticketingsystem.service;

import org.springframework.stereotype.Service;

@Service
public class VendorService {
    public void releaseTickets(int rate) {
        // Vendor-specific logic to release tickets at a given rate.
        System.out.println("Releasing tickets at rate: " + rate);
    }
}