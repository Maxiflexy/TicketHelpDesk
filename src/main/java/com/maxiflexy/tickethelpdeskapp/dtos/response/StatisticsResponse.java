package com.maxiflexy.tickethelpdeskapp.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsResponse {

    private String number_of_tickets;
    private String number_of_assigned_tickets;
    private String number_of_tickets_not_assigned;
    private String number_of_assigned_tickets_pending;
    private String number_of_assigned_tickets_closed;
    private String number_of_org_users;
    private String total_number_org;

}
