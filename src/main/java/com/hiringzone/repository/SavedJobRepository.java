package com.hiringzone.repository;

import com.hiringzone.model.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Integer> {
    Page<SavedJob> findByUserId(Integer userId, Pageable pageable);
    Optional<SavedJob> findByUserIdAndJobId(Integer userId, Integer jobId);
    void deleteByUserIdAndJobId(Integer userId, Integer jobId);
}
