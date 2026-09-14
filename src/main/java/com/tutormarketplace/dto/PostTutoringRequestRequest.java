package com.tutormarketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostTutoringRequestRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotBlank(message = "Skill topic is required")
    private String skillTopic;
    
    @NotNull(message = "Bounty points is required")
    @Positive(message = "Bounty points must be positive")
    private Long bountyPoints;
}
