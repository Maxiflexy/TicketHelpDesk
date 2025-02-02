package com.maxiflexy.tickethelpdeskapp.model;

import com.maxiflexy.tickethelpdeskapp.constants.Priority;
import com.maxiflexy.tickethelpdeskapp.constants.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "ticket")
public class Ticket extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", referencedColumnName = "id", nullable = false)
    private Organization organization;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "app_name", nullable = false)
    private String applicationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_assigned", nullable = false)
    private boolean assignStatus;

    @Column(name = "file_name", nullable = false, length = 20)
    private String fileTitle;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

}
