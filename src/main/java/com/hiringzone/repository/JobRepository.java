package com.hiringzone.repository;

import com.hiringzone.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer>, JpaSpecificationExecutor<Job> {

    List<Job> findByCompanyId(Integer companyId);
    Page<Job> findByCompanyId(Integer companyId, Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE j.company.id = :companyId " +
           "AND (:search IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Job> findByCompanyIdWithSearch(
            @Param("companyId") Integer companyId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT j FROM Job j WHERE (:search IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:flagged IS NULL OR j.flagged = :flagged)")
    Page<Job> findAllAdmin(
            @Param("search") String search,
            @Param("flagged") Boolean flagged,
            Pageable pageable
    );

    long countByCompanyId(Integer companyId);
    long countByCompanyUserIdAndCreatedAtAfter(Integer userId, java.time.LocalDateTime date);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.category = :category")
    long countByCategory(@Param("category") String category);
}
