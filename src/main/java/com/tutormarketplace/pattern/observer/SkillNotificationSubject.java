package com.tutormarketplace.pattern.observer;

import com.tutormarketplace.model.SkillSubscription;
import com.tutormarketplace.model.TutoringSession;
import com.tutormarketplace.repository.NotificationRepository;
import com.tutormarketplace.repository.SkillSubscriptionRepository;
import com.tutormarketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Subject (Observable) for managing skill notification observers.
 * When a new tutoring request is posted, notifies all subscribers for that skill.
 */
@Component
@RequiredArgsConstructor
public class SkillNotificationSubject {
    
    private final SkillSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final Map<String, Set<SkillNotificationObserver>> observers = new HashMap<>();
    
    /**
     * Attach an observer for a specific skill.
     * 
     * @param skill the skill to observe
     * @param observer the observer to attach
     */
    public void attach(String skill, SkillNotificationObserver observer) {
        observers.computeIfAbsent(skill, k -> new HashSet<>())
            .add(observer);
    }
    
    /**
     * Detach an observer for a specific skill.
     * 
     * @param skill the skill to stop observing
     * @param observer the observer to detach
     */
    public void detach(String skill, SkillNotificationObserver observer) {
        Set<SkillNotificationObserver> skillObservers = observers.get(skill);
        if (skillObservers != null) {
            skillObservers.remove(observer);
        }
    }
    
    /**
     * Notify all observers subscribed to a particular skill.
     * Automatically looks up observers from the database.
     * 
     * @param session the newly posted tutoring session
     */
    public void notifyObservers(TutoringSession session) {
        String skill = session.getSkillTopic();
        
        // Load subscribers from database
        List<SkillSubscription> subscriptions = subscriptionRepository.findBySkillName(skill);
        
        subscriptions.forEach(subscription -> {
            SkillNotificationObserver observer = new StudentSkillNotificationObserver(
                subscription.getUser().getId(),
                skill,
                notificationRepository,
                userRepository
            );
            observer.update(session);
        });
    }
    
    /**
     * Notify all observers in memory cache (for testing/manual invocation).
     * 
     * @param skill the skill topic
     * @param session the tutoring session
     */
    public void notifyObserversBySkill(String skill, TutoringSession session) {
        Set<SkillNotificationObserver> skillObservers = observers.get(skill);
        if (skillObservers != null) {
            skillObservers.forEach(observer -> observer.update(session));
        }
    }
    
    /**
     * Get all observers for a skill.
     * 
     * @param skill the skill name
     * @return set of observers
     */
    public Set<SkillNotificationObserver> getObservers(String skill) {
        return observers.getOrDefault(skill, new HashSet<>());
    }
    
    /**
     * Get total count of observers.
     * 
     * @return total observer count across all skills
     */
    public int getObserverCount() {
        return observers.values().stream()
            .mapToInt(Set::size)
            .sum();
    }
}
