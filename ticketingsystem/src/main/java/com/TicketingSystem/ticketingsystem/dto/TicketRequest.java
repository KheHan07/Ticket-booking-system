package com.TicketingSystem.ticketingsystem.dto;

public class TicketRequest {
    private int noOfTickets;  // Number of tickets to be created
    private String eventName;  // Event name
    private double price;  // Ticket price
    private boolean isSold;  // Status if ticket is sold or not

    // Getters and Setters
    public int getNoOfTickets() {
        return noOfTickets;
    }

    public void setNoOfTickets(int noOfTickets) {
        this.noOfTickets = noOfTickets;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }
}
