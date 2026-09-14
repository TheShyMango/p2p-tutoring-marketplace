package com.tutormarketplace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaiseDisputeRequest {
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    private String description;
}
