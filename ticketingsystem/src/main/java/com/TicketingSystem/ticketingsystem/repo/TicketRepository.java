package com.TicketingSystem.ticketingsystem.repo;

import com.TicketingSystem.ticketingsystem.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Find all unsold tickets
    List<Ticket> findByIsSoldFalse();

    // Find all unsold tickets by event name
    List<Ticket> findByEventNameAndIsSoldFalse(String eventName);
}
