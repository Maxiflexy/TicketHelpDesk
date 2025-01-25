package com.maxiflexy.tickethelpdeskapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Slf4j
@MappedSuperclass
@Data
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_seq_generator")
    @SequenceGenerator(name = "entity_seq_generator", sequenceName = "ticket_seq", allocationSize = 1)
    private Long id;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "creation_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void onCreate() {
        try {
            createdDate = LocalDateTime.now();
            createdBy = SecurityContextHolder.getContext().getAuthentication().getName();
            updatedDate = LocalDateTime.now();
            updatedBy = SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }





}
