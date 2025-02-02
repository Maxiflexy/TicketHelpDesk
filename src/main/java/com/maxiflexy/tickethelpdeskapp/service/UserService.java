package com.maxiflexy.tickethelpdeskapp.service;

import com.maxiflexy.tickethelpdeskapp.dtos.global.ApiResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.global.PaginatedEntity;
import com.maxiflexy.tickethelpdeskapp.dtos.request.PasswordResetRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.request.UserRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.response.UserIdResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.response.UserResponse;
import com.maxiflexy.tickethelpdeskapp.model.User;

import java.util.List;

public interface UserService {

    ApiResponse<UserIdResponse> createUser(UserRequest userRequest);

    PaginatedEntity<List<UserResponse>> fetchUser(String id, String email, String orgId, String page, String size);

    ApiResponse<UserIdResponse> deactivateUser(String id);

    ApiResponse<UserIdResponse> activateUser(String id);

    ApiResponse<UserIdResponse> updateUser(String id, UserRequest userRequest);

    ApiResponse<UserIdResponse> resetPassword(PasswordResetRequest passwordRequest);

    User fetchUserByEmail(String email);

}
