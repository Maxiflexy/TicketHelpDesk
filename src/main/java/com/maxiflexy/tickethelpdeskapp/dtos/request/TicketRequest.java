package com.maxiflexy.tickethelpdeskapp.dtos.request;

import com.maxiflexy.tickethelpdeskapp.constants.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {

    private String title;
    private String appName;
    private Priority priority;
    private String description;
    private String fileName;
    private MultipartFile file;
}
