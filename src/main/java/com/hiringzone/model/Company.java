package com.hiringzone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    private String industry;
    private String description;
    private String website;
    private String location;
    private boolean verified;

    public String getCompanyName() {
        return name;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : "";
    }

    public boolean isSuspended() {
        return user != null && user.isSuspended();
    }

    public long getJobCount() {
        return 0; // Requires repo
    }
}
