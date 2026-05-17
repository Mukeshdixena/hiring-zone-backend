package com.hiringzone.repository;

import com.hiringzone.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    Page<Application> findByUserId(Integer userId, Pageable pageable);
    Page<Application> findByUserIdAndStatus(Integer userId, String status, Pageable pageable);
    Page<Application> findByJobId(Integer jobId, Pageable pageable);
    Page<Application> findByJobIdAndStatus(Integer jobId, String status, Pageable pageable);
    
    java.util.Optional<Application> findByUserIdAndJobId(Integer userId, Integer jobId);

    long countByUserId(Integer userId);
    long countByUserIdAndStatus(Integer userId, String status);
    
    long countByJobCompanyUserId(Integer userId);
    long countByJobCompanyUserIdAndStatus(Integer userId, String status);
    
    long countByJobCompanyUserIdAndAppliedAtAfter(Integer userId, java.time.LocalDateTime date);
    long countByJobCompanyUserIdAndStatusAndAppliedAtAfter(Integer userId, String status, java.time.LocalDateTime date);

    Page<Application> findByJobCompanyUserId(Integer userId, Pageable pageable);
    
    long countByStatus(String status);
    long countByAppliedAtBefore(java.time.LocalDateTime date);
}
