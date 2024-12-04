package com.TicketingSystem.ticketingsystem.dto;

import lombok.Data;

@Data
public class TicketConfigDto {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;
}