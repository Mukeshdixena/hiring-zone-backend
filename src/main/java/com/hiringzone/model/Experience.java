package com.hiringzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seeker_experiences")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private SeekerProfile profile;

    private String company;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean current;
    
    @Column(columnDefinition = "TEXT")
    private String description;
}
