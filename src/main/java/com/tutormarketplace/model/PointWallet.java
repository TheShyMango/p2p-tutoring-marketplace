package com.tutormarketplace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_wallets", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointWallet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(nullable = false)
    private Long availableBalance;
    
    @Column(nullable = false)
    private Long lockedBalance;
    
    @Column(nullable = false)
    private Long totalEarned;
    
    @Column(nullable = false)
    private Long totalSpent;
    
    @Version
    @Column(nullable = false)
    private Long version;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        availableBalance = 0L;
        lockedBalance = 0L;
        totalEarned = 0L;
        totalSpent = 0L;
        version = 0L;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean canWithdraw(Long amount) {
        return availableBalance >= amount;
    }
    
    public boolean hasEnoughBalance(Long amount) {
        return availableBalance + lockedBalance >= amount;
    }
}