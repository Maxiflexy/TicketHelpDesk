package com.maxiflexy.tickethelpdeskapp.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FileDownloadResponse {
    private byte[] fileContent;
    private String fileName;
}
