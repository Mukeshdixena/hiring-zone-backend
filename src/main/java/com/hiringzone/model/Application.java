package com.hiringzone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Null for guest apply

    private String status; // Pending, Reviewed, Shortlisted, Rejected, Hired
    
    @Column(columnDefinition = "TEXT")
    private String coverLetter;
    
    private String resumePath;
    private String guestName;
    private String guestEmail;
    private LocalDateTime appliedAt;

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
        if (status == null) status = "Pending";
    }
}
