package com.hiringzone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seeker_profiles")
public class SeekerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    private String phone;
    private String location;
    private String portfolioUrl;
    private String githubUrl;
    private String linkedinUrl;
    
    @Column(columnDefinition = "TEXT")
    private String skills; // Comma separated

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new java.util.ArrayList<>();
}
