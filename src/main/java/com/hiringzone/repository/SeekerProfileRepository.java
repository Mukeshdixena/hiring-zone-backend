package com.hiringzone.repository;

import com.hiringzone.model.SeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SeekerProfileRepository extends JpaRepository<SeekerProfile, Integer> {
    Optional<SeekerProfile> findByUserId(Integer userId);
}
