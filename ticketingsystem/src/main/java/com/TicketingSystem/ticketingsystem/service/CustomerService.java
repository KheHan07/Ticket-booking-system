package com.TicketingSystem.ticketingsystem.service;

import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    public void retrieveTickets(int rate) {
        // Customer-specific logic to retrieve tickets at a given rate.
        System.out.println("Retrieving tickets at rate: " + rate);
    }
}