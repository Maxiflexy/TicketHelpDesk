package com.maxiflexy.tickethelpdeskapp.repository;

import com.maxiflexy.tickethelpdeskapp.model.TicketUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketUserRepository extends JpaRepository<TicketUser, Long> {

    List<TicketUser> findByTicketId(Long ticketId);
}
