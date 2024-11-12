package com.TicketingSystem.ticketingsystem.service;

import com.TicketingSystem.ticketingsystem.config.TicketConfig;
import com.TicketingSystem.ticketingsystem.dto.TicketDTO;
import com.TicketingSystem.ticketingsystem.entity.Ticket;
import com.TicketingSystem.ticketingsystem.repo.TicketRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TicketConsumerService {

    @Autowired
    private TicketConfig ticketConfig;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ModelMapper modelMapper;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startConsumingTickets() {
        scheduler.scheduleAtFixedRate(this::purchaseTickets, 0, ticketConfig.getCustomerRetrievalRate(), TimeUnit.SECONDS);
    }

    private synchronized void purchaseTickets() {
        List<Ticket> availableTickets = ticketRepository.findByIsSoldFalse();
        if (!availableTickets.isEmpty()) {
            Ticket ticket = availableTickets.get(0);
            ticket.setSold(true);
            ticketRepository.save(ticket);
        }
    }

    public List<TicketDTO> getSoldTickets() {
        return ticketRepository.findAll().stream()
                .filter(Ticket::isSold)
                .map(ticket -> modelMapper.map(ticket, TicketDTO.class))
                .collect(Collectors.toList());
    }
}
