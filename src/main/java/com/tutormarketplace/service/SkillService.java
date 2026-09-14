package com.tutormarketplace.service;

import com.tutormarketplace.model.SkillSubscription;
import com.tutormarketplace.model.SkillTag;
import com.tutormarketplace.model.User;
import com.tutormarketplace.repository.SkillSubscriptionRepository;
import com.tutormarketplace.repository.SkillTagRepository;
import com.tutormarketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    
    private final SkillTagRepository skillTagRepository;
    private final SkillSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    
    /**
     * Add a skill for a user (student proficiency).
     */
    @Transactional
    public SkillTag addSkill(Long userId, String skillName, String proficiencyLevel) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Check if already exists
        if (skillTagRepository.findByUserIdAndSkillName(userId, skillName).isPresent()) {
            throw new IllegalArgumentException("Skill already exists for user");
        }
        
        SkillTag skill = SkillTag.builder()
            .user(user)
            .skillName(skillName)
            .proficiencyLevel(proficiencyLevel)
            .build();
        
        return skillTagRepository.save(skill);
    }
    
    /**
     * Remove a skill from a user.
     */
    @Transactional
    public void removeSkill(Long userId, String skillName) {
        SkillTag skill = skillTagRepository.findByUserIdAndSkillName(userId, skillName)
            .orElseThrow(() -> new IllegalArgumentException("Skill not found for user"));
        
        skillTagRepository.delete(skill);
    }
    
    /**
     * Subscribe to a skill (tutor wants to receive notifications for this skill).
     */
    @Transactional
    public SkillSubscription subscribeToSkill(Long userId, String skillName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Check if already subscribed
        if (subscriptionRepository.findByUserIdAndSkillName(userId, skillName).isPresent()) {
            throw new IllegalArgumentException("Already subscribed to this skill");
        }
        
        SkillSubscription subscription = SkillSubscription.builder()
            .user(user)
            .skillName(skillName)
            .build();
        
        return subscriptionRepository.save(subscription);
    }
    
    /**
     * Unsubscribe from a skill.
     */
    @Transactional
    public void unsubscribeFromSkill(Long userId, String skillName) {
        SkillSubscription subscription = subscriptionRepository.findByUserIdAndSkillName(userId, skillName)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        
        subscriptionRepository.delete(subscription);
    }
    
    /**
     * Get all skills for a user.
     */
    public List<SkillTag> getSkillsByUser(Long userId) {
        return skillTagRepository.findByUserId(userId);
    }
    
    /**
     * Get all skill names for a user.
     */
    public List<String> getSkillNamesByUser(Long userId) {
        return skillTagRepository.findByUserId(userId)
            .stream()
            .map(SkillTag::getSkillName)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all users with a specific skill.
     */
    public List<User> getUsersWithSkill(String skillName) {
        return skillTagRepository.findBySkillName(skillName)
            .stream()
            .map(SkillTag::getUser)
            .collect(Collectors.toList());
    }
    
    /**
     * Get subscription count for a skill.
     */
    public Long getSkillSubscriptionCount(String skillName) {
        return subscriptionRepository.findBySkillName(skillName)
            .stream()
            .count();
    }
}
