package com.maxiflexy.tickethelpdeskapp.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.maxiflexy.tickethelpdeskapp.constants.AppConstant;
import com.maxiflexy.tickethelpdeskapp.constants.Status;
import com.maxiflexy.tickethelpdeskapp.dtos.global.ApiResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.global.ApiResponseTickets;
import com.maxiflexy.tickethelpdeskapp.dtos.request.TicketRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.response.*;
import com.maxiflexy.tickethelpdeskapp.exceptions.TicketNotFoundException;
import com.maxiflexy.tickethelpdeskapp.exceptions.UnauthorizedException;
import com.maxiflexy.tickethelpdeskapp.model.MailModel;
import com.maxiflexy.tickethelpdeskapp.model.Ticket;
import com.maxiflexy.tickethelpdeskapp.model.TicketUser;
import com.maxiflexy.tickethelpdeskapp.model.User;
import com.maxiflexy.tickethelpdeskapp.repository.*;
import com.maxiflexy.tickethelpdeskapp.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class TicketServiceImpl extends UserHandler implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketUserRepository ticketUserRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final AmazonS3 amazonS3;
    private final EmailSenderService emailSenderService;
    private final Executor taskExecutor;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    public TicketServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                             TicketRepository ticketRepository, TicketUserRepository ticketUserRepository,
                             UserRepository userRepository1, OrganizationRepository organizationRepository,
                             AmazonS3 amazonS3, EmailSenderService emailSenderService,
                             @Qualifier("threadPoolTaskExecutor")Executor taskExecutor) {
        super(userRepository, roleRepository);
        this.ticketRepository = ticketRepository;
        this.ticketUserRepository = ticketUserRepository;
        this.userRepository = userRepository1;
        this.organizationRepository = organizationRepository;
        this.amazonS3 = amazonS3;
        this.emailSenderService = emailSenderService;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public ApiResponse<TicketIdResponse> createTicket(TicketRequest ticketRequestDTO) {

        User user = fetchUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        Ticket ticket = new Ticket();
        ticket.setTitle(ticketRequestDTO.getTitle());
        ticket.setApplicationName(ticketRequestDTO.getAppName());
        ticket.setPriority(ticketRequestDTO.getPriority());
        ticket.setDescription(ticketRequestDTO.getDescription());
        ticket.setStatus(Status.OPEN);
        ticket.setOrganization(user.getOrganization());
        ticket.setCreatedBy(user.getEmail());
        ticket.setCreatedDate(LocalDateTime.now());

        ticketRepository.save(ticket);

        if (ticketRequestDTO.getFile() != null && !ticketRequestDTO.getFile().isEmpty()) {
            taskExecutor.execute(() -> {
                String fileUrl = uploadFileToS3(ticketRequestDTO.getFile());
                ticket.setFileUrl(fileUrl);
                ticket.setFileTitle(ticketRequestDTO.getFileName() != null
                        ? ticketRequestDTO.getFileName()
                        : ticketRequestDTO.getFile().getOriginalFilename());
                ticketRepository.save(ticket); // Update ticket with file URL
            });
        }

        taskExecutor.execute(() -> sendTicketCreationEmail(user, ticket, fetchCcEmails()));

        TicketIdResponse ticketIdResponse = new TicketIdResponse();
        ticketIdResponse.setId(ticket.getId());

        return ApiResponse
                .<TicketIdResponse>builder()
                .status(true)
                .responseMessage("ticket created successfully")
                .responseCode(AppConstant.successResponseCode)
                .data(ticketIdResponse)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<TicketIdResponse> updateStatus(Long ticketId, String status) {
        User user = fetchUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status.");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket with ID " + ticketId + " not found."));

        switch (status.toUpperCase()){
            case "IN_PROGRESS":
            case "RESOLVED":
                if (!AppConstant.INFOMETICS_ORG_NAME.equalsIgnoreCase(user.getOrganization().getOrgName())) {
                    throw new UnauthorizedException("Only users from INFOMETICS can update the ticket to " + status + " status.");
                }
                break;
            case "CLOSED":
                if (user.getEmail().equalsIgnoreCase(ticket.getCreatedBy())) {
                    break;
                }
                if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole().getRoleName())
                        && user.getOrganization().getId().equals(ticket.getOrganization().getId())) {
                    break;
                }
                throw new UnauthorizedException("Only the ticket creator or an admin from the ticket's organization can close the ticket.");
            default:
                throw new IllegalArgumentException("Unhandled status: " + status);
        }

        //Ticket ticket = optionalTicket.get();
        ticket.setStatus(Status.valueOf(status));
        ticket.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
        ticket.setUpdatedDate(LocalDateTime.now());
        ticketRepository.save(ticket);

        User creator = fetchUserByEmail(ticket.getCreatedBy());
        taskExecutor.execute(() -> sendStatusUpdateEmail(creator, ticket, status));

        TicketIdResponse ticketIdResponse = new TicketIdResponse();
        ticketIdResponse.setId(ticketId);

        return ApiResponse
                .<TicketIdResponse>builder()
                .status(true)
                .responseMessage("ticket status updated successfully")
                .responseCode(AppConstant.successResponseCode)
                .data(ticketIdResponse)
                .build();
    }

    @Override
    public ApiResponseTickets<List<TicketResponseDTO>> getAllTickets(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        User user = fetchUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        String orgName = user.getOrganization().getOrgName();
        Long orgId = user.getOrganization().getId();

        Page<Ticket> ticketPage;

        if ("INFOMETICS".equalsIgnoreCase(orgName)) {
            ticketPage = ticketRepository.findAllByOrderByCreatedDateDescIdDesc(pageRequest);
        } else {
            ticketPage = ticketRepository.findAllByOrganizationIdOrderByCreatedDateDescIdDesc(orgId, pageRequest);
        }

        //Page<Ticket> ticketPage = ticketRepository.findAllByOrderByCreatedDateDescIdDesc(pageRequest);

        List<TicketResponseDTO> ticketDTOs = ticketPage.getContent().stream()
                .map(ticket -> TicketResponseDTO.builder()
                        .ticketId(ticket.getId())
                        .title(ticket.getTitle())
                        .appName(ticket.getApplicationName())
                        .priority(ticket.getPriority().name())
                        .status(ticket.getStatus().name())
                        .assignedStatus(ticket.isAssignStatus())
                        .description(ticket.getDescription())
                        .fileTitle(ticket.getFileTitle())
                        .fileUrl(ticket.getFileUrl())
                        .build()
                ).toList();


        return ApiResponseTickets
                .<List<TicketResponseDTO>>builder()
                .status(true)
                .responseCode(AppConstant.successResponseCode)
                .responseMessage("Tickets retrieved successfully")
                .page(page)
                .size(size)
                .total_count((int) ticketPage.getTotalElements())
                .data(ticketDTOs)
                .build();
    }

    @Override
    public ApiResponse<TicketResponseDTO> getTicketById(Long ticketId) {

        User user = fetchUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        String orgName = user.getOrganization().getOrgName();
        Long orgId = user.getOrganization().getId();

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket with ID " + ticketId + " not found."));

        // Check organization rules
        if (!AppConstant.INFOMETICS_ORG_NAME.equalsIgnoreCase(orgName)) {
            if (!ticket.getOrganization().getId().equals(orgId)) {
                throw new TicketNotFoundException(
                        String.format("No such ticket with ID %d exists for your organization.", ticketId));
            }
        }

        List<String> fullNames = null;

        if (AppConstant.INFOMETICS_ORG_NAME.equalsIgnoreCase(orgName)) {
            List<TicketUser> ticketUsers = ticketUserRepository.findByTicketId(ticketId);
            fullNames = ticketUsers.stream()
                    .map(ticketUser -> {
                        User assignedUser = ticketUser.getUser();
                        return assignedUser.getFirstName() + " " + assignedUser.getLastName();
                    })
                    .toList();
        }

        TicketResponseDTO ticketResponse = TicketResponseDTO.builder()
                .ticketId(ticket.getId())
                .title(ticket.getTitle())
                .appName(ticket.getApplicationName())
                .priority(ticket.getPriority().name())
                .status(ticket.getStatus().name())
                .assignedStatus(ticket.isAssignStatus())
                .description(ticket.getDescription())
                .fileTitle(ticket.getFileTitle())
                .fileUrl(ticket.getFileUrl())
                .fullNames(fullNames)
                .build();

        return ApiResponse.
                <TicketResponseDTO>builder()
                .status(true)
                .responseCode(AppConstant.successResponseCode)
                .responseMessage("Ticket retrieved successfully")
                .data(ticketResponse)
                .build();
    }

    @Override
    public ApiResponse<StatisticsResponse> getStatistics() {

        User currentUser = fetchUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        String role = currentUser.getRole().getRoleName();
        Long orgId = currentUser.getOrganization().getId();
        String orgName = currentUser.getOrganization().getOrgName();

        StatisticsResponse statisticsResponse;

        switch (role) {

            case "ROLE_SUPER_ADMIN", "ROLE_SUPPORT_INFOMETICS", "ROLE_INFOMETICS_USER":
                statisticsResponse = StatisticsResponse.builder()
                        .number_of_tickets(String.valueOf(ticketRepository.count()))
                        .number_of_assigned_tickets(String.valueOf(ticketRepository.countByAssignStatus(true)))
                        .number_of_tickets_not_assigned(String.valueOf(ticketRepository.countByAssignStatus(false)))
                        .number_of_assigned_tickets_pending(String.valueOf(ticketRepository.countByStatusNot(Status.CLOSED)))
                        .number_of_assigned_tickets_closed(String.valueOf(ticketRepository.countByStatus(Status.CLOSED)))
                        .number_of_org_users(String.valueOf(userRepository.count()))
                        .total_number_org(String.valueOf(organizationRepository.count()))
                        .build();
                break;

            case "ROLE_ADMIN":
                if(AppConstant.INFOMETICS_ORG_NAME.equalsIgnoreCase(orgName)) {
                    statisticsResponse = StatisticsResponse.builder()
                            .number_of_tickets(String.valueOf(ticketRepository.count()))
                            .number_of_assigned_tickets(String.valueOf(ticketRepository.countByAssignStatus(true)))
                            .number_of_tickets_not_assigned(String.valueOf(ticketRepository.countByAssignStatus(false)))
                            .number_of_assigned_tickets_pending(String.valueOf(ticketRepository.countByStatusNot(Status.CLOSED)))
                            .number_of_assigned_tickets_closed(String.valueOf(ticketRepository.countByStatus(Status.CLOSED)))
                            .number_of_org_users(String.valueOf(userRepository.count()))
                            .total_number_org(String.valueOf(organizationRepository.count()))
                            .build();
                    break;
                }

                statisticsResponse = StatisticsResponse.builder()
                        .number_of_tickets(String.valueOf(ticketRepository.countByOrganizationId(orgId)))
                        .number_of_assigned_tickets(String.valueOf(ticketRepository.countByOrganizationIdAndAssignStatus(orgId, true)))
                        .number_of_tickets_not_assigned(String.valueOf(ticketRepository.countByOrganizationIdAndAssignStatus(orgId, false)))
                        .number_of_assigned_tickets_pending(String.valueOf(ticketRepository.countByOrganizationIdAndStatusNot(orgId, Status.CLOSED)))
                        .number_of_assigned_tickets_closed(String.valueOf(ticketRepository.countByOrganizationIdAndStatus(orgId, Status.CLOSED)))
                        .number_of_org_users(String.valueOf(userRepository.countByOrganizationId(orgId)))
                        .build();
                break;

            case "ROLE_SUPPORT_CLIENT":
                statisticsResponse = StatisticsResponse.builder()
                        .number_of_tickets(String.valueOf(ticketRepository.countByOrganizationId(orgId)))
                        .number_of_assigned_tickets(String.valueOf(ticketRepository.countByOrganizationIdAndAssignStatus(orgId, true)))
                        .number_of_tickets_not_assigned(String.valueOf(ticketRepository.countByOrganizationIdAndAssignStatus(orgId, false)))
                        .number_of_assigned_tickets_pending(String.valueOf(ticketRepository.countByOrganizationIdAndStatusNot(orgId, Status.CLOSED)))
                        .number_of_assigned_tickets_closed(String.valueOf(ticketRepository.countByOrganizationIdAndStatus(orgId, Status.CLOSED)))
                        .number_of_org_users(String.valueOf(userRepository.countByOrganizationId(orgId)))
                        .build();
                break;

            default:
                throw new UnsupportedOperationException("Role not supported for this operation.");
        }

        return ApiResponse.
                <StatisticsResponse>builder()
                .status(true)
                .responseCode(AppConstant.successResponseCode)
                .responseMessage("Statistics retrieved successfully")
                .data(statisticsResponse)
                .build();
    }

    @Override
    public ApiResponse<TicketIdResponse> updateTicket(Long ticketId, TicketRequest ticketUpdateRequest) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));

        if (ticketUpdateRequest.getTitle() != null) {
            ticket.setTitle(ticketUpdateRequest.getTitle());
        }
        if (ticketUpdateRequest.getAppName() != null) {
            ticket.setApplicationName(ticketUpdateRequest.getAppName());
        }
        if (ticketUpdateRequest.getPriority() != null) {
            ticket.setPriority(ticketUpdateRequest.getPriority());
        }
        if (ticketUpdateRequest.getDescription() != null) {
            ticket.setDescription(ticketUpdateRequest.getDescription());
        }

        if (ticketUpdateRequest.getFile() != null && !ticketUpdateRequest.getFile().isEmpty()) {
            taskExecutor.execute(() -> {
                String fileUrl = uploadFileToS3(ticketUpdateRequest.getFile());
                ticket.setFileUrl(fileUrl);
                ticket.setFileTitle(ticketUpdateRequest.getFileName() != null
                        ? ticketUpdateRequest.getFileName()
                        : ticketUpdateRequest.getFile().getOriginalFilename());
                ticketRepository.save(ticket); // Save updated ticket with file info
            });
        }

        ticketRepository.save(ticket);

        TicketIdResponse ticketIdResponse = new TicketIdResponse();
        ticketIdResponse.setId(ticket.getId());

        return ApiResponse
                .<TicketIdResponse>builder()
                .status(true)
                .responseMessage("Ticket updated successfully")
                .responseCode(AppConstant.successResponseCode)
                .data(ticketIdResponse)
                .build();
    }

    @Override
    public FileDownloadResponse downloadFile(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket with ID " + ticketId + " not found."));

        if (ticket.getFileUrl() == null || ticket.getFileUrl().isEmpty()) {
            throw new TicketNotFoundException("No file associated with ticket ID: " + ticketId);
        }

        //String fileKey = extractFileKeyFromUrl(ticket.getFileUrl());
        String fileKey = ticket.getFileUrl();
        S3Object s3Object = amazonS3.getObject(bucketName, fileKey);
        try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            byte[] fileContent = IOUtils.toByteArray(inputStream);
            String fileName = ticket.getFileTitle();
            return FileDownloadResponse.builder()
                    .fileContent(fileContent)
                    .fileName(fileName)
                    .build();
        } catch (IOException e) {
            log.error("Error downloading file for ticket ID: {}", ticketId, e);
            throw new RuntimeException("Failed to download file");
        }
    }

    @Override
    public ApiResponse<TicketIdResponse> deleteFile(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket with ID " + ticketId + " not found."));

        if (ticket.getFileUrl() == null || ticket.getFileUrl().isEmpty()) {
            throw new TicketNotFoundException("No file associated with ticket ID: " + ticketId);
        }

        String fileKey = ticket.getFileUrl();
        amazonS3.deleteObject(bucketName, fileKey);

        ticket.setFileUrl(null);
        ticket.setFileTitle(null);
        ticketRepository.save(ticket);

        TicketIdResponse ticketIdResponse = new TicketIdResponse();
        ticketIdResponse.setId(ticket.getId());

        return ApiResponse
                .<TicketIdResponse>builder()
                .status(true)
                .responseMessage("Ticket file deleted successfully")
                .responseCode(AppConstant.successResponseCode)
                .data(ticketIdResponse)
                .build();
    }

    @Override
    public ApiResponse<TicketIdResponse> assignUserToTicket(Long ticketId, Long userId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with ID: " + ticketId));

        User currentUser = fetchUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!AppConstant.INFOMETICS_ORG_NAME.equalsIgnoreCase(currentUser.getOrganization().getOrgName())) {
            throw new UnauthorizedException("Not authorized to assign ticket.");
        }

        User user = fetchUserById(userId);
        if (!AppConstant.INFOMETICS_ORG_NAME.equalsIgnoreCase(user.getOrganization().getOrgName())) {
            throw new UnauthorizedException("User cannot be assigned to a ticket.");
        }

        ticket.setAssignStatus(true);
        ticketRepository.save(ticket);

        TicketUser ticketUser = new TicketUser();
        ticketUser.setTicket(ticket);
        ticketUser.setUser(user);
        ticketUserRepository.save(ticketUser);

        taskExecutor.execute(() -> sendTicketAssignmentUpdateEmail(user, ticket));

        TicketIdResponse ticketIdResponse = new TicketIdResponse();
        ticketIdResponse.setId(ticket.getId());

        return ApiResponse
                .<TicketIdResponse>builder()
                .status(true)
                .responseMessage("Ticket assigned successfully")
                .responseCode(AppConstant.successResponseCode)
                .data(ticketIdResponse)
                .build();
    }

    @Override
    public ApiResponse<TicketStatusResponse> getTicketStatuses() {

        User user = fetchUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        String orgName = user.getOrganization().getOrgName();

        List<String> statuses;
        if (AppConstant.INFOMETICS_ORG_NAME.equalsIgnoreCase(orgName)) {
            statuses = List.of(Status.IN_PROGRESS.name(), Status.RESOLVED.name());
        } else {
            statuses = List.of(Status.CLOSED.name());
        }

        TicketStatusResponse response = TicketStatusResponse.builder()
                .statuses(statuses)
                .build();

        return ApiResponse
                .<TicketStatusResponse>builder()
                .status(true)
                .responseMessage("Ticket statuses retrieved successfully")
                .responseCode(AppConstant.successResponseCode)
                .data(response)
                .build();
    }

    private String uploadFileToS3(MultipartFile multipartFile) {
        File file = convertMultipartFileToFile(multipartFile);
        String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file));
        boolean deleted = file.delete();
        System.out.println("File deleted?: " + deleted);
        //return amazonS3.getUrl(bucketName, fileName).toString();
        return fileName;
    }

    private File convertMultipartFileToFile(MultipartFile file){
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }catch (IOException e){
            log.error("Error converting multipart file to file", e);
        }
        return convertedFile;
    }

    private boolean isValidStatus(String status) {
        return Status.IN_PROGRESS.name().equalsIgnoreCase(status)
                || Status.RESOLVED.name().equalsIgnoreCase(status)
                || Status.CLOSED.name().equalsIgnoreCase(status);
    }

    private void sendTicketCreationEmail(User user, Ticket ticket, List<String> ccEmails) {

        MailModel model = new MailModel();
        try {
            log.info("Sending ticket creation email");

            model.setFrom("helpdesk@infometics.net");
            model.setSubject("Ticket Created Notification");
            model.setUseTemplate(true);
            model.setTemplateName("ticket_creation_notification.tpl");

            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("clientName", user.getFirstName() + " " + user.getLastName());
            messageMap.put("ticketId", String.valueOf(ticket.getId()));
            //messageMap.put("yourLink", "https://your-application-link/tickets/" + ticket.getId());

            model.setMessageMap(messageMap);
            model.setTo(new String[]{user.getEmail()});
            model.setCc(ccEmails.toArray(new String[0]));

            emailSenderService.sendEmail(model);
            log.info("Ticket creation email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send ticket creation email", e);
        }
    }

    private void sendStatusUpdateEmail(User user, Ticket ticket, String status) {
        MailModel model = new MailModel();
        try {
            log.info("Sending ticket status update email");

            model.setFrom("helpdesk@infometics.net");
            model.setSubject("Ticket Status Update Notification");
            model.setUseTemplate(true);
            model.setTemplateName("ticket_status_notification.tpl");

            // Prepare template values
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("clientName", user.getFirstName() + " " + user.getLastName());
            messageMap.put("ticketId", String.valueOf(ticket.getId()));
            messageMap.put("ticketStatus", status);

            model.setMessageMap(messageMap);
            model.setTo(new String[]{user.getEmail()});

            emailSenderService.sendEmail(model);
            log.info("Ticket status update email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send ticket status update email", e);
        }
    }


    private void sendTicketAssignmentUpdateEmail(User user, Ticket ticket) {
        MailModel model = new MailModel();
        try {
            log.info("Sending ticket status update email");

            model.setFrom("helpdesk@infometics.net");
            model.setSubject("Ticket Assignment Notification");
            model.setUseTemplate(true);
            model.setTemplateName("ticket_assignment_notification.tpl");

            // Prepare template values
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("infometicsUserName", user.getFirstName() + " " + user.getLastName());
            messageMap.put("ticketId", String.valueOf(ticket.getId()));

            model.setMessageMap(messageMap);
            model.setTo(new String[]{user.getEmail()});

            emailSenderService.sendEmail(model);
            log.info("Ticket status update email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send ticket status update email", e);
        }
    }


    private List<String> fetchCcEmails() {
        return userRepository.findAllByRoleId(4L).stream()
                .map(User::getEmail)
                .toList();
    }
}