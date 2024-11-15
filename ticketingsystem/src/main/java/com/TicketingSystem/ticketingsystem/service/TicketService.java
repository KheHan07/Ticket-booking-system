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
    private TicketRepository ticketRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<TicketDTO> getAvailableTickets() {
        List<Ticket> availableTickets = ticketRepository.findByIsSoldFalse();
        return availableTickets.stream()
                .map(ticket -> modelMapper.map(ticket, TicketDTO.class))
                .collect(Collectors.toList());
    }

    public void generateTickets(int noOfTickets, String eventName, double price) {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < noOfTickets; i++) {
            Ticket ticket = new Ticket();
            ticket.setEventName(eventName);
            ticket.setPrice(price);
            ticket.setSold(false);
            tickets.add(ticket);
        }
        ticketRepository.saveAll(tickets);
    }

    public int deleteUnsoldTicketsByEvent(String eventName) {
        List<Ticket> unsoldTickets = ticketRepository.findByEventNameAndIsSoldFalse(eventName);
        int deletedCount = unsoldTickets.size();
        ticketRepository.deleteAll(unsoldTickets);
        return deletedCount;
    }


    public boolean buyTickets(String eventName, int quantity) {
        List<Ticket> availableTickets = ticketRepository.findByEventNameAndIsSoldFalse(eventName);
        if (availableTickets.size() < quantity) {
            return false;  // Not enough tickets available
        }
        List<Ticket> ticketsToBuy = availableTickets.subList(0, quantity);
        ticketsToBuy.forEach(ticket -> ticket.setSold(true));
        ticketRepository.saveAll(ticketsToBuy);
        return true;
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
}
