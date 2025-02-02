package com.maxiflexy.tickethelpdeskapp.service.impl;

import com.maxiflexy.tickethelpdeskapp.constants.AppConstant;
import com.maxiflexy.tickethelpdeskapp.constants.Role;
import com.maxiflexy.tickethelpdeskapp.dtos.global.ApiResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.global.PaginatedEntity;
import com.maxiflexy.tickethelpdeskapp.dtos.request.PasswordResetRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.request.UserRequest;
import com.maxiflexy.tickethelpdeskapp.dtos.response.UserIdResponse;
import com.maxiflexy.tickethelpdeskapp.dtos.response.UserResponse;
import com.maxiflexy.tickethelpdeskapp.exceptions.BadRequestException;
import com.maxiflexy.tickethelpdeskapp.exceptions.DuplicateResourceException;
import com.maxiflexy.tickethelpdeskapp.model.MailModel;
import com.maxiflexy.tickethelpdeskapp.model.User;
import com.maxiflexy.tickethelpdeskapp.repository.RoleRepository;
import com.maxiflexy.tickethelpdeskapp.repository.UserRepository;
import com.maxiflexy.tickethelpdeskapp.service.OrganizationService;
import com.maxiflexy.tickethelpdeskapp.service.RoleService;
import com.maxiflexy.tickethelpdeskapp.service.UserService;
import com.maxiflexy.tickethelpdeskapp.utils.AppUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends UserHandler implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final EntityManager em;
    private final OrganizationService organizationService;
    private final EmailSenderService emailSenderService;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserRepository userRepository1, PasswordEncoder passwordEncoder, RoleService roleService, EntityManager em, OrganizationService organizationService, EmailSenderService emailSenderService) {
        super(userRepository, roleRepository);
        this.userRepository = userRepository1;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.em = em;
        this.organizationService = organizationService;
        this.emailSenderService = emailSenderService;
    }

    @Override
    public ApiResponse<UserIdResponse> createUser(UserRequest userRequest) {
        try {
            User currentUser = fetchLoggedInUser();

            if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Email already exists");
            }

            UserIdResponse userIdResponse = new UserIdResponse();
            User user = new User();
            BeanUtils.copyProperties(userRequest, user);
            String password = AppUtil.createRandomString(AppConstant.passwordLength);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(roleService.fetchRoleById(userRequest.getRole_id()));
            if (currentUser.getRole().getRoleName().equals(Role.ROLE_SUPER_ADMIN.name())) {
                user.setOrganization(organizationService.getOrganizationById(userRequest.getOrg_id()));
            } else {
                user.setOrganization(currentUser.getOrganization());
            }

            user.setUsername(userRequest.getEmail().toLowerCase());
            user.setEmail(userRequest.getEmail().toLowerCase());
            user.setStatus(true);
            user.setFirstLogin(true);
            user = userRepository.save(user);
            userIdResponse.setId(user.getId());
            sendMessage(user, password);
            return ApiResponse
                    .<UserIdResponse>builder()
                    .responseCode(AppConstant.successResponseCode)
                    .responseMessage("User created successfully")
                    .status(true)
                    .data(userIdResponse)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void sendMessage(User user,String message) {
        MailModel model = new MailModel();
        try {

            log.info("Sending email");
            model.setFrom("michael.afolabi@infometics.net");
            model.setSubject("TICKET HELPDESK");
            model.setUseTemplate(true);
            model.setTemplateName("user_creation_notification.tpl");

            Map<String, String> mMap = new HashMap<>();
            mMap.put("initiator", user.getFirstName());
            mMap.put("password", message);
            mMap.put("email", user.getEmail());

            model.setMessageMap(mMap);
            model.setTo(new String[]{user.getEmail()});
            emailSenderService.sendEmail(model);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public PaginatedEntity<List<UserResponse>> fetchUser(String id, String email, String orgId, String page, String size) {
        User currentUser = fetchLoggedInUser();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);
        List<Predicate> predicates = new ArrayList<>();
        if (!currentUser.getRole().getRoleName().equals(Role.ROLE_SUPER_ADMIN.name())) {
            orgId = String.valueOf(currentUser.getOrganization().getId());
        }

        if (id != null && !id.isEmpty()) {
            predicates.add(cb.equal(userRoot.get("id"), id));
        }
        if (email != null && !email.isEmpty()) {
            predicates.add(cb.equal(userRoot.get("email"), email));
        }
        if (orgId != null && !orgId.isEmpty()) {
            predicates.add(cb.equal(userRoot.get("organization"), organizationService.getOrganizationById(Long.parseLong(orgId))));
        }

        int pageNumber = page != null && !page.isEmpty() ? Integer.parseInt(page) - 1 : 0;
        int pageSize = size != null && !size.isEmpty() ? Integer.parseInt(size) : 10;

        TypedQuery<User> query = em.createQuery(cq.where(predicates.stream().toArray(Predicate[]::new)))
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize);

        TypedQuery<User> queryCount = em.createQuery(cq.where(predicates.stream().toArray(Predicate[]::new)));

        List<User> users = query.getResultList();
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);
            userResponse.setOrg_name(user.getOrganization().getOrgName());
            userResponse.setRole_name(user.getRole().getRoleName());
            responses.add(userResponse);
        }

        return PaginatedEntity.<List<UserResponse>>builder()
                .status(true)
                .responseMessage("Users fetched")
                .responseCode(AppConstant.successResponseCode)
                .page(pageNumber + 1)
                .size(pageSize)
                .total(queryCount.getResultList().size())
                .data(responses)
                .build();
    }

    @Override
    public ApiResponse<UserIdResponse> deactivateUser(String id) {
        UserIdResponse userIdResponse = new UserIdResponse();
        User currentUser = fetchLoggedInUser();
        User user = fetchUserById(id);

        if (!currentUser.getRole().getRoleName().equals(Role.ROLE_SUPER_ADMIN.name()) && !currentUser.getOrganization().equals(user.getOrganization())) {
            throw new BadRequestException("Unable to deactivate user of another organization");
        }
        if (user.isStatus()) {
            user.setStatus(false);
        } else {
            throw new BadRequestException("User is not active");
        }

        user.setStatus(false);
        userRepository.save(user);
        userIdResponse.setId(user.getId());

        return ApiResponse
                .<UserIdResponse>builder()
                .status(true)
                .responseMessage("User deactivation successful")
                .responseCode(AppConstant.successResponseCode)
                .data(userIdResponse)
                .build();

    }

    @Override
    public ApiResponse<UserIdResponse> activateUser(String id) {
        UserIdResponse userIdResponse = new UserIdResponse();
        User currentUser = fetchLoggedInUser();
        User user = fetchUserById(id);
        if (!currentUser.getRole().getRoleName().equals(Role.ROLE_SUPER_ADMIN.name()) && !currentUser.getOrganization().equals(user.getOrganization())) {
            throw new BadRequestException("Unable to activate user of another organization");
        }
        if (!user.isStatus()) {
            user.setStatus(true);
        } else {
            throw new BadRequestException("User is already active");
        }

        user.setStatus(true);
        userRepository.save(user);
        userIdResponse.setId(user.getId());

        return ApiResponse
                .<UserIdResponse>builder()
                .status(true)
                .responseMessage("User activation successful")
                .responseCode(AppConstant.successResponseCode)
                .data(userIdResponse)
                .build();
    }

    @Override
    public ApiResponse<UserIdResponse> updateUser(String id, UserRequest userRequest) {

        User user = fetchUserById(id);
        User currentUser = fetchLoggedInUser();
        UserIdResponse userIdResponse = new UserIdResponse();

        if (!currentUser.getRole().getRoleName().equals(Role.ROLE_SUPER_ADMIN.name()) && !currentUser.getOrganization().equals(user.getOrganization())) {
            throw new BadRequestException("Unable to update user of another organization");
        }

        if ( userRequest.getEmail() != null && !userRequest.getEmail().isBlank()) {
            user.setEmail(userRequest.getEmail());
        }

        if (userRequest.getLastName() != null && !userRequest.getLastName().isBlank()) {
            user.setLastName(userRequest.getLastName());
        }

        if (userRequest.getFirstName() != null && !userRequest.getFirstName().isBlank()) {
            user.setFirstName(userRequest.getFirstName());
        }

        if (userRequest.getRole_id() != 0) {
            user.setRole(roleService.fetchRoleById(userRequest.getRole_id()));
        }

        if (userRequest.getOrg_id() != 0) {
            user.setOrganization(organizationService.getOrganizationById(userRequest.getOrg_id()));
        }
        userRepository.save(user);
        userIdResponse.setId(user.getId());

        return ApiResponse
                .<UserIdResponse>builder()
                .status(true)
                .responseMessage("User update successful")
                .responseCode(AppConstant.successResponseCode)
                .data(userIdResponse)
                .build();
    }

    @Override
    public ApiResponse<UserIdResponse> resetPassword(PasswordResetRequest passwordRequest) {
        User user = fetchUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
        user.setFirstLogin(false);
        userRepository.save(user);
        UserIdResponse userIdResponse = new UserIdResponse();
        userIdResponse.setId(user.getId());
        return ApiResponse
                .<UserIdResponse>builder()
                .status(true)
                .responseMessage("User update successful")
                .responseCode(AppConstant.successResponseCode)
                .data(userIdResponse)
                .build();
    }

}
