package com.TicketingSystem.ticketingsystem.controller;

import com.TicketingSystem.ticketingsystem.dto.TicketConfigDto;
import com.TicketingSystem.ticketingsystem.dto.StartStopDto;
import com.TicketingSystem.ticketingsystem.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("/configure")
    public String configureTicketingSystem(@RequestBody TicketConfigDto configDto) {
        ticketService.configure(configDto);
        return "Configuration completed";
    }
    @PostMapping("/set-vendors-buyers")
    public String setVendorsAndBuyers(@RequestBody StartStopDto dto) {
        ticketService.setVendorsAndBuyers(dto);
        return "Vendors and buyers updated successfully.";
    }


    @PostMapping("/start")
    public String startTicketingSystem(@RequestBody StartStopDto dto) {
        return ticketService.startSystem(dto);
    }

    @PostMapping("/stop")
    public String stopTicketingSystem() {
        ticketService.stopSystem();
        return "System stopped";
    }
}