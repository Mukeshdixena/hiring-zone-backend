package com.hiringzone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderListDTO {
    private Integer id;
    private String companyName;
    private String industry;
    private String email;
    private boolean verified;
    private boolean suspended;
    private long jobCount;
}
