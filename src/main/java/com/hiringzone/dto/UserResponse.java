package com.hiringzone.dto;

import com.hiringzone.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String email;
    private String name;
    private Role role;
    private String companyName; // Only for employers
}
