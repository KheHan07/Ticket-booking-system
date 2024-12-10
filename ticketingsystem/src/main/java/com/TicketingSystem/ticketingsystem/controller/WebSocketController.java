package com.TicketingSystem.ticketingsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendTicketUpdate(String message) {
        messagingTemplate.convertAndSend("/topic/ticket-updates", message);
    }

    public void sendStatsUpdate(String jsonStats) {
        messagingTemplate.convertAndSend("/topic/ticket-updates", jsonStats);
    }
}
