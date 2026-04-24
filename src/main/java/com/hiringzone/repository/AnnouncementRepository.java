package com.hiringzone.repository;

import com.hiringzone.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    List<Announcement> findByTargetInOrderByCreatedAtDesc(List<String> targets);
}
