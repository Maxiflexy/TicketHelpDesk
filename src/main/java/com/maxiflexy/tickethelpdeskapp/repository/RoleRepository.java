package com.maxiflexy.tickethelpdeskapp.repository;

import com.maxiflexy.tickethelpdeskapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findById(Long id);

}
