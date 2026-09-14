package com.tutormarketplace.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "session_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionNote {
    
    @Id
    private String id;
    
    private Long tutoringSessionId;
    
    private Long studentId;
    
    private Long tutorId;
    
    private String topicsCovered;
    
    private String studentProgress;
    
    private String tutorObservations;
    
    private String homeworkAssigned;
    
    private Integer sessionDuration;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
