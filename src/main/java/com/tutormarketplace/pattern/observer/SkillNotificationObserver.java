package com.tutormarketplace.pattern.observer;

import com.tutormarketplace.model.TutoringSession;

/**
 * Observer interface for receiving notifications when a new tutoring request is posted.
 */
public interface SkillNotificationObserver {
    
    /**
     * Update the observer when a new matching request is posted.
     * 
     * @param session the newly posted tutoring session
     */
    void update(TutoringSession session);
    
    /**
     * Get the subscriber's user ID.
     * 
     * @return user ID
     */
    Long getSubscriberUserId();
    
    /**
     * Get the skill this observer is interested in.
     * 
     * @return skill name
     */
    String getSubscribedSkill();
}
