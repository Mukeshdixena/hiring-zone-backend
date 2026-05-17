package com.hiringzone.repository;

import com.hiringzone.model.Role;
import com.hiringzone.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    long countByRole(Role role);

    long countByRoleAndCreatedAtBefore(Role role, LocalDateTime date);

    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR :status = '' OR " +
           " (:status = 'suspended' AND u.suspended = true) OR " +
           " (:status = 'active' AND u.suspended = false))")
    Page<User> findAllFiltered(
            @Param("search") String search,
            @Param("status") String status,
            Pageable pageable
    );
}
