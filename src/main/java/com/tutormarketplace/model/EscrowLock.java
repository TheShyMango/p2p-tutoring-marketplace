package com.tutormarketplace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "escrow_locks", indexes = {
    @Index(name = "idx_state", columnList = "state"),
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_tutor_id", columnList = "tutor_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EscrowLock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutoring_session_id", nullable = false, unique = true)
    private TutoringSession tutoringSession;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private User tutor;
    
    @Column(nullable = false)
    private Long lockedPoints;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EscrowState state;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime lockedAt;
    
    private LocalDateTime releasedAt;
    
    private LocalDateTime reversedAt;
    
    private String reason;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lockedAt = LocalDateTime.now();
        state = EscrowState.LOCKED;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isLocked() {
        return state == EscrowState.LOCKED;
    }
    
    public boolean isReleased() {
        return state == EscrowState.RELEASED;
    }
    
    public boolean isReversed() {
        return state == EscrowState.REVERSED;
    }
}
