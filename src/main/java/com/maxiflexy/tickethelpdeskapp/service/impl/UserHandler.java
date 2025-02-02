package com.maxiflexy.tickethelpdeskapp.service.impl;


import com.maxiflexy.tickethelpdeskapp.model.Role;
import com.maxiflexy.tickethelpdeskapp.model.User;
import com.maxiflexy.tickethelpdeskapp.repository.RoleRepository;
import com.maxiflexy.tickethelpdeskapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;



public abstract class UserHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserHandler(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public Role fetchRoleById(long id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            return role.get();
        }
        throw new EntityNotFoundException("Role id " + id + " not found");
    }

    public User fetchUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UsernameNotFoundException("User not found");
    }

    public User fetchUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        throw new EntityNotFoundException("User not found");
    }

    public User fetchUserById(String id) {
        return fetchUserById(Long.parseLong(id));
    }

    public User fetchLoggedInUser(){
        return fetchUserByEmail(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }


}
