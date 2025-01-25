package com.maxiflexy.tickethelpdeskapp.controllers;

import com.infometics.helpdesk.constants.Priority;
import com.infometics.helpdesk.dtos.global.ApiResponse;
import com.infometics.helpdesk.dtos.global.ApiResponseTickets;
import com.infometics.helpdesk.dtos.request.TicketRequest;
import com.infometics.helpdesk.dtos.response.*;
import com.infometics.helpdesk.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/v1/ticket-helpdesk/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<TicketIdResponse>> createTicket(
            @RequestParam String title,
            @RequestParam String appName,
            @RequestParam Priority priority,
            @RequestParam String description,
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) MultipartFile file) {


        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setTitle(title);
        ticketRequest.setAppName(appName);
        ticketRequest.setPriority(priority);
        ticketRequest.setDescription(description);
        ticketRequest.setFileName(fileName);
        ticketRequest.setFile(file);

        return ResponseEntity.ok(ticketService.createTicket(ticketRequest));
    }


    @PutMapping("/update_status")
    public ResponseEntity<ApiResponse<TicketIdResponse>> updateTicketStatus(
            @RequestParam Long ticketId,
            @RequestParam String status){
        return ResponseEntity.ok(ticketService.updateStatus(ticketId, status));
    }


    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<TicketResponseDTO>> getTicketById(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.getTicketById(ticketId));
    }


    @GetMapping("/tickets")
    public ResponseEntity<ApiResponseTickets<List<TicketResponseDTO>>> getAllTickets(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page <= 0 || size <= 0) {
            return ResponseEntity.badRequest().body(
                    ApiResponseTickets.<List<TicketResponseDTO>>builder()
                            .status(false)
                            .responseCode("400")
                            .responseMessage("Invalid pagination parameters")
                            .build()
            );
        }
        return ResponseEntity.ok(ticketService.getAllTickets(page, size));
    }

    @PatchMapping(value = "/{ticketId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<TicketIdResponse>> updateTicket(
            @PathVariable Long ticketId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String appName,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) MultipartFile file) {

        TicketRequest ticketUpdateRequest = new TicketRequest();
        ticketUpdateRequest.setTitle(title);
        ticketUpdateRequest.setAppName(appName);
        ticketUpdateRequest.setPriority(priority);
        ticketUpdateRequest.setDescription(description);
        ticketUpdateRequest.setFileName(fileName);
        ticketUpdateRequest.setFile(file);

        return ResponseEntity.ok(ticketService.updateTicket(ticketId, ticketUpdateRequest));
    }

    @GetMapping("/{ticketId}/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long ticketId) {
        FileDownloadResponse response = ticketService.downloadFile(ticketId);
        ByteArrayResource fileContent = new ByteArrayResource(response.getFileContent());
        return ResponseEntity.ok()
                .contentLength(response.getFileContent().length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileContent);
    }

    @DeleteMapping("/{ticketId}/delete_file")
    public ResponseEntity<ApiResponse<TicketIdResponse>> deleteFile(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.deleteFile(ticketId));
    }

    @PostMapping("/assign_ticket")
    @PreAuthorize("hasAnyRole('SUPPORT_INFOMETICS','ADMIN')")
    public ResponseEntity<ApiResponse<TicketIdResponse>> assignUserToTicket(
            @RequestParam Long ticketId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(ticketService.assignUserToTicket(ticketId, userId));
    }

    @GetMapping("statuses")
    public ResponseEntity<ApiResponse<TicketStatusResponse>> getStatus(){
        return ResponseEntity.ok(ticketService.getTicketStatuses());
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getStatistics() {
        return ResponseEntity.ok(ticketService.getStatistics());
    }


}
