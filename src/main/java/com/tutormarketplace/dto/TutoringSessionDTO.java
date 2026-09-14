package com.tutormarketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutoringSessionDTO {
    private Long id;
    private UserDTO student;
    private UserDTO tutor;
    private String title;
    private String description;
    private String skillTopic;
    private Long bountyPoints;
    private String state;
    private LocalDateTime creationTime;
    private LocalDateTime acceptanceTime;
    private LocalDateTime startTime;
    private LocalDateTime completionTime;
    private Integer durationMinutes;
    private String rating;
    private String feedback;
    private LocalDateTime createdAt;
}
