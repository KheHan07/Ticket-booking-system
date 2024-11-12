package com.TicketingSystem.ticketingsystem.controller;

import com.TicketingSystem.ticketingsystem.dto.TicketDTO;
import com.TicketingSystem.ticketingsystem.dto.TicketRequest;
import com.TicketingSystem.ticketingsystem.service.TicketConsumerService;
import com.TicketingSystem.ticketingsystem.service.TicketProducerService;
import com.TicketingSystem.ticketingsystem.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "api/v1/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketProducerService ticketProducerService;

    @Autowired
    private TicketConsumerService ticketConsumerService;

    // Get available tickets
    @GetMapping("/getavailabletickets")
    public List<TicketDTO> getAvailableTickets() {
        return ticketService.getAvailableTickets();
    }

    // Create a single ticket
    @PostMapping("/createticket")
    public TicketDTO createTicket(@RequestBody TicketDTO ticketDTO) {
        return ticketService.createTicket(ticketDTO);
    }

    // Sell a ticket by ID
    @PutMapping("/{id}/sell")
    public TicketDTO sellTicket(@PathVariable Long id) {
        return ticketService.sellTicket(id);
    }

    // Start the ticket production process (accepting ticket details)
    @PostMapping("/startProduction")
    public String startProduction(@RequestBody TicketRequest ticketRequest) {
        // Call the service to produce the requested number of tickets
        ticketService.startProducingTickets(
                ticketRequest.getNoOfTickets(),
                ticketRequest.getEventName(),
                ticketRequest.getPrice(),
                ticketRequest.isSold()
        );
        return "Ticket production started for " + ticketRequest.getNoOfTickets() + " tickets.";
    }

    // Start the ticket consumption process
    @PostMapping("/startConsumption")
    public String startConsumption() {
        ticketConsumerService.startConsumingTickets();
        return "Ticket consumption started.";
    }

    // Get all sold tickets
    @GetMapping("/getsoldtickets")
    public List<TicketDTO> getSoldTickets() {
        return ticketConsumerService.getSoldTickets();
    }
}
