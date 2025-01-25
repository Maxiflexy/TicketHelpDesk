package com.maxiflexy.tickethelpdeskapp.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponseDTO {
    private Long ticketId;
    private String title;
    private String appName;
    private String priority;
    private String status;
    private Boolean assignedStatus;
    private String description;
    private String fileTitle;
    private String fileUrl;
    private List<String> fullNames;
}

