package com.maxiflexy.tickethelpdeskapp.repository;

import com.maxiflexy.tickethelpdeskapp.constants.Status;
import com.maxiflexy.tickethelpdeskapp.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findAllByOrderByCreatedDateDescIdDesc(Pageable pageable);

    Page<Ticket> findAllByOrganizationIdOrderByCreatedDateDescIdDesc(Long orgId, PageRequest pageRequest);

    long countByAssignStatus(boolean assignStatus);
    long countByOrganizationId(Long orgId);
    long countByOrganizationIdAndAssignStatus(Long orgId, boolean assignStatus);
    long countByOrganizationIdAndStatusNot(Long orgId, Status status);
    long countByOrganizationIdAndStatus(Long orgId, Status status);
    long countByStatusNot(Status status);
    long countByStatus(Status status);
}
