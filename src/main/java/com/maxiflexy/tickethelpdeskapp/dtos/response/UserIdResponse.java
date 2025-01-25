package com.maxiflexy.tickethelpdeskapp.dtos.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class UserIdResponse {

    @JsonAlias("user_id")
    private Long id;
}
