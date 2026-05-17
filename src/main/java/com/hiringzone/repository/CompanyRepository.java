package com.hiringzone.repository;

import com.hiringzone.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    Optional<Company> findByUserId(Integer userId);

    @Query("SELECT c FROM Company c WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(c.user.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:verified IS NULL OR c.verified = :verified)")
    Page<Company> findAllFiltered(
            @Param("search") String search,
            @Param("verified") Boolean verified,
            Pageable pageable
    );
}
