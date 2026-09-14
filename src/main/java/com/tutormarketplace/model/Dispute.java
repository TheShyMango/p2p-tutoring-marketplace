package com.tutormarketplace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "disputes", indexes = {
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_tutoring_session_id", columnList = "tutoring_session_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispute {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutoring_session_id", nullable = false)
    private TutoringSession tutoringSession;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_id", nullable = false)
    private User raisedBy;
    
    @Column(nullable = false)
    private String reason;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String resolution;
    
    private LocalDateTime resolvedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = DisputeStatus.OPEN;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isOpen() {
        return status == DisputeStatus.OPEN;
    }
    
    public boolean isResolved() {
        return status == DisputeStatus.RESOLVED;
    }
}
