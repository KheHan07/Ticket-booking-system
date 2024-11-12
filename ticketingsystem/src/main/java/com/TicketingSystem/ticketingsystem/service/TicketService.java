package com.TicketingSystem.ticketingsystem.service;

import com.TicketingSystem.ticketingsystem.dto.TicketDTO;
import com.TicketingSystem.ticketingsystem.entity.Ticket;
import com.TicketingSystem.ticketingsystem.repo.TicketRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;  // Only this declaration is needed

    @Autowired
    private ModelMapper modelMapper;

    public List<TicketDTO> getAvailableTickets() {
        List<Ticket> availableTickets = ticketRepository.findByIsSoldFalse();
        return availableTickets.stream()
                .map(ticket -> modelMapper.map(ticket, TicketDTO.class))
                .collect(Collectors.toList());
    }

    public TicketDTO createTicket(TicketDTO ticketDTO) {
        Ticket ticket = modelMapper.map(ticketDTO, Ticket.class);
        ticket.setSold(false); // Ensures ticket is unsold upon creation
        ticket = ticketRepository.save(ticket);
        return modelMapper.map(ticket, TicketDTO.class);
    }

    public TicketDTO sellTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        if (ticket.isSold()) {
            throw new RuntimeException("Ticket already sold");
        }
        ticket.setSold(true);
        ticket = ticketRepository.save(ticket);
        return modelMapper.map(ticket, TicketDTO.class);
    }

    // This method will create a batch of tickets
    public void startProducingTickets(int noOfTickets, String eventName, double price, boolean isSold) {
        List<Ticket> ticketsToCreate = new ArrayList<>();

        for (int i = 0; i < noOfTickets; i++) {
            Ticket ticket = new Ticket();
            ticket.setEventName(eventName);
            ticket.setPrice(price);
            ticket.setSold(isSold);
            ticketsToCreate.add(ticket);
        }

        // Save all the tickets in the repository
        ticketRepository.saveAll(ticketsToCreate);
    }
}
