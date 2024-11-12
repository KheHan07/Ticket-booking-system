package com.TicketingSystem.ticketingsystem.service;

import com.TicketingSystem.ticketingsystem.config.TicketConfig;
import com.TicketingSystem.ticketingsystem.entity.Ticket;
import com.TicketingSystem.ticketingsystem.repo.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TicketProducerService {

    @Autowired
    private TicketConfig ticketConfig;

    @Autowired
    private TicketRepository ticketRepository;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startProducingTickets() {
        scheduler.scheduleAtFixedRate(this::releaseTickets, 0, ticketConfig.getTicketReleaseRate(), TimeUnit.SECONDS);
    }

    private synchronized void releaseTickets() {
        long currentTicketCount = ticketRepository.count();
        if (currentTicketCount < ticketConfig.getMaxTicketCapacity()) {
            Ticket ticket = new Ticket();
            ticket.setEventName("Event Name");  // Default or configurable event name
            ticket.setPrice(50.0);              // Default or configurable price
            ticket.setSold(false);
            ticketRepository.save(ticket);
        }
    }
}
