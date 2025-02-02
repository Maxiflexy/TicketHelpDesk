package com.maxiflexy.tickethelpdeskapp.service;

import com.maxiflexy.tickethelpdeskapp.dtos.global.ApiResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.global.ApiResponseTickets;
import com.maxiflexy.tickethelpdeskapp.dtos.request.TicketRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.response.*;

import java.util.List;

public interface TicketService {

    ApiResponse<TicketIdResponse> createTicket(TicketRequest ticketRequestDTO);

    ApiResponse<TicketIdResponse> updateStatus(Long ticketId, String status);

    ApiResponseTickets<List<TicketResponseDTO>> getAllTickets(int page, int size);

    ApiResponse<TicketResponseDTO> getTicketById(Long ticketId);

    ApiResponse<TicketIdResponse> updateTicket(Long ticketId, TicketRequest ticketUpdateRequest);

    FileDownloadResponse downloadFile(Long ticketId);

    ApiResponse<TicketIdResponse> deleteFile(Long ticketId);

    ApiResponse<TicketIdResponse> assignUserToTicket(Long ticketId, Long userId);

    ApiResponse<TicketStatusResponse> getTicketStatuses();

    ApiResponse<StatisticsResponse> getStatistics();
}
