package com.tutormarketplace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "skill_tags", uniqueConstraints = {
    @UniqueConstraint(name = "unique_user_skill", columnNames = {"user_id", "skill_name"})
}, indexes = {
    @Index(name = "idx_skill_name", columnList = "skill_name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String skillName;
    
    private String proficiencyLevel;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
