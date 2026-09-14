package com.tutormarketplace.repository;

import com.tutormarketplace.model.SkillSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillSubscriptionRepository extends JpaRepository<SkillSubscription, Long> {
    List<SkillSubscription> findByUserId(Long userId);
    List<SkillSubscription> findBySkillName(String skillName);
    Optional<SkillSubscription> findByUserIdAndSkillName(Long userId, String skillName);
}
