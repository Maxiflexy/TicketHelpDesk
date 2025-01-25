package com.maxiflexy.tickethelpdeskapp.service;

import com.infometics.helpdesk.dtos.global.ApiResponse;
import com.infometics.helpdesk.dtos.global.PaginatedEntity;
import com.infometics.helpdesk.dtos.request.PasswordResetRequest;
import com.infometics.helpdesk.dtos.request.UserRequest;
import com.infometics.helpdesk.dtos.response.UserIdResponse;
import com.infometics.helpdesk.dtos.response.UserResponse;
import com.infometics.helpdesk.model.User;

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
