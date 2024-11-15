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

    // Generate tickets for an event
    @PostMapping("/generateTickets")
    public String generateTickets(@RequestBody TicketRequest ticketRequest) {
        ticketService.generateTickets(
                ticketRequest.getNoOfTickets(),
                ticketRequest.getEventName(),
                ticketRequest.getPrice()
        );
        return ticketRequest.getNoOfTickets() + " tickets generated for event: " + ticketRequest.getEventName();
    }

    // Delete unsold tickets by event name
// Delete all unsold tickets for a specific event
    @DeleteMapping("/deleteEvent")
    public String deleteEvent(@RequestParam String eventName) {
        int deletedCount = ticketService.deleteUnsoldTicketsByEvent(eventName);
        if (deletedCount > 0) {
            return "All unsold tickets for event '" + eventName + "' have been deleted.";
        } else {
            return "No unsold tickets found for event '" + eventName + "'.";
        }
    }


    // Buy tickets by event name and quantity
    @PostMapping("/buyTickets")
    public String buyTickets(@RequestParam String eventName, @RequestParam int quantity) {
        boolean success = ticketService.buyTickets(eventName, quantity);
        return success ? quantity + " tickets bought for event '" + eventName + "'."
                : "Not enough available tickets for event '" + eventName + "'.";
    }

    // Get all sold tickets
    @GetMapping("/getsoldtickets")
    public List<TicketDTO> getSoldTickets() {
        return ticketConsumerService.getSoldTickets();
    }
}
