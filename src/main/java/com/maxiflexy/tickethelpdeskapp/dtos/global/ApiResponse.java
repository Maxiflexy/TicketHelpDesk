package com.maxiflexy.tickethelpdeskapp.dtos.global;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> implements Serializable {

    private String responseCode;
    private String responseMessage;
    private Boolean status;
    private final LocalDateTime time = LocalDateTime.now();
    private T data;


}