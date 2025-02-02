package com.maxiflexy.tickethelpdeskapp.controllers;

import com.maxiflexy.tickethelpdeskapp.dtos.global.ApiResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.global.PaginatedEntity;
import com.maxiflexy.tickethelpdeskapp.dtos.request.PasswordResetRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.request.UserRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.response.UserIdResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.response.UserResponse;
import com.maxiflexy.tickethelpdeskapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/v1/ticket-helpdesk/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("create-user")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<UserIdResponse>> createUser(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity
                .ok()
                .body(userService.createUser(userRequest));
    }


    @GetMapping()
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<PaginatedEntity<List<UserResponse>>> getUser(
                @RequestParam(required = false) String id,
                @RequestParam(required = false) String email,
                @RequestParam(required = false) String org_id,
                @RequestParam(required = false) String page,
                @RequestParam(required = false) String size
    ) {
        return ResponseEntity
                .ok(userService.fetchUser(id, email, org_id, page, size));
    }

    @PutMapping("/deactivate/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN', 'SUPPORT_INFOMETICS')")
    public ResponseEntity<ApiResponse<UserIdResponse>> deactivateUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @PutMapping("/activate/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<UserIdResponse>> activateUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<UserIdResponse>> updateUser(@PathVariable String id, @Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse<UserIdResponse>> resetPassword(@Valid @RequestBody PasswordResetRequest passwordRequest) {
        return ResponseEntity.ok(userService.resetPassword(passwordRequest));
    }



}
