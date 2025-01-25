package com.maxiflexy.tickethelpdeskapp.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TicketIdResponse {

    @JsonProperty("ticketId")
    private Long id;
}
