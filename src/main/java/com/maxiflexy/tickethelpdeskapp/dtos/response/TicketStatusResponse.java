package com.maxiflexy.tickethelpdeskapp.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketStatusResponse {

    @JsonProperty("statuses")
    private List<String> statuses;
}
