package com.TicketingSystem.ticketingsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
@CrossOrigin
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Make the method public so it can be called from other services
    public void sendTicketUpdate(String message) {
        messagingTemplate.convertAndSend("/topic/ticket-updates", message);
    }
}
