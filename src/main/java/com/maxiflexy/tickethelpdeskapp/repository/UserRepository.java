package com.maxiflexy.tickethelpdeskapp.repository;

import com.maxiflexy.tickethelpdeskapp.model.Organization;
import com.maxiflexy.tickethelpdeskapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameAndPassword(String username, String password);
    List<User> findByOrganization(Organization organization);

    @Query("SELECT u FROM User u WHERE u.organization.id = :orgId")
    List<User> findAllByOrganizationId(@Param("orgId") Long orgId);

    @Query("SELECT u FROM User u WHERE u.role.id = :roleId")
    List<User> findAllByRoleId(@Param("roleId") Long roleId);

    long countByOrganizationId(Long orgId);
}
