package com.tutormarketplace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tutoring_sessions", indexes = {
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_tutor_id", columnList = "tutor_id"),
    @Index(name = "idx_state", columnList = "state"),
    @Index(name = "idx_skill_topic", columnList = "skill_topic")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutoringSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private User tutor;
    
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String skillTopic;
    
    @Column(nullable = false)
    private Long bountyPoints;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionState state;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationTime;
    
    private LocalDateTime acceptanceTime;
    
    private LocalDateTime startTime;
    
    private LocalDateTime completionTime;
    
    private Integer durationMinutes;
    
    private BigDecimal rating;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        creationTime = LocalDateTime.now();
        state = SessionState.OPEN;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isOpen() {
        return state == SessionState.OPEN;
    }
    
    public boolean isInProgress() {
        return state == SessionState.IN_PROGRESS;
    }
    
    public boolean isInDispute() {
        return state == SessionState.IN_DISPUTE;
    }
    
    public boolean isClosed() {
        return state == SessionState.CLOSED;
    }
}
