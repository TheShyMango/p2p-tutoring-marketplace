package com.tutormarketplace.pattern.observer;

import com.tutormarketplace.model.TutoringSession;
import com.tutormarketplace.model.Notification;
import com.tutormarketplace.model.NotificationType;
import com.tutormarketplace.model.User;
import com.tutormarketplace.repository.NotificationRepository;
import com.tutormarketplace.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Concrete Observer: Notifies a subscriber when a tutoring request matching their skill is posted.
 */
@Getter
@RequiredArgsConstructor
public class StudentSkillNotificationObserver implements SkillNotificationObserver {
    
    private final Long subscriberUserId;
    private final String subscribedSkill;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    @Override
    public void update(TutoringSession session) {
        // Verify the session skill matches the subscription
        if (!session.getSkillTopic().equalsIgnoreCase(subscribedSkill)) {
            return; // Mismatch, do not notify
        }
        
        // Retrieve the subscriber
        User subscriber = userRepository.findById(subscriberUserId)
            .orElse(null);
        
        if (subscriber == null) {
            return; // Subscriber no longer exists
        }
        
        // Create and save notification
        Notification notification = Notification.builder()
            .user(subscriber)
            .notificationType(NotificationType.SKILL_MATCH)
            .title("New Request: " + session.getSkillTopic())
            .message("A new tutoring request for " + session.getSkillTopic() + " has been posted. " +
                    "Bounty: " + session.getBountyPoints() + " points")
            .relatedSession(session)
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();
        
        notificationRepository.save(notification);
    }
}
