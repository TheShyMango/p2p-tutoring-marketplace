package com.tutormarketplace.service;

import com.tutormarketplace.dto.TutoringSessionDTO;
import com.tutormarketplace.model.*;
import com.tutormarketplace.pattern.command.CommandExecutionException;
import com.tutormarketplace.pattern.command.CommandInvoker;
import com.tutormarketplace.pattern.command.TransferPointsCommand;
import com.tutormarketplace.pattern.observer.SkillNotificationSubject;
import com.tutormarketplace.pattern.state.InvalidStateTransitionException;
import com.tutormarketplace.pattern.state.SessionStateContext;
import com.tutormarketplace.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TutoringSessionService {
    
    private final TutoringSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final PointWalletRepository walletRepository;
    private final EscrowLockRepository escrowRepository;
    private final PointLedgerRepository ledgerRepository;
    private final SkillSubscriptionRepository subscriptionRepository;
    
    private final SessionStateContext stateContext;
    private final CommandInvoker commandInvoker;
    private final SkillNotificationSubject notificationSubject;
    
    /**
     * Post a new tutoring request (bounty).
     */
    @Transactional
    public TutoringSession postRequest(Long studentId, String title, String description, 
                                       String skillTopic, Long bountyPoints) {
        
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        
        PointWallet wallet = walletRepository.findByUserId(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        
        // Verify student has enough points
        if (!wallet.canWithdraw(bountyPoints)) {
            throw new IllegalArgumentException(
                "Insufficient points. Required: " + bountyPoints + ", Available: " + wallet.getAvailableBalance()
            );
        }
        
        // Create session
        TutoringSession session = TutoringSession.builder()
            .student(student)
            .title(title)
            .description(description)
            .skillTopic(skillTopic)
            .bountyPoints(bountyPoints)
            .state(SessionState.OPEN)
            .build();
        
        session = sessionRepository.save(session);
        
        // Lock bounty points using Command Pattern
        try {
            TransferPointsCommand lockCommand = TransferPointsCommand.builder()
                .studentId(studentId)
                .tutorId(studentId) // Placeholder for lock only
                .amount(bountyPoints)
                .tutoringSession(session)
                .transferType(TransferPointsCommand.TransferType.LOCK_BOUNTY)
                .walletRepository(walletRepository)
                .ledgerRepository(ledgerRepository)
                .escrowRepository(escrowRepository)
                .build();
            
            commandInvoker.execute(lockCommand);
        } catch (CommandExecutionException e) {
            throw new RuntimeException("Failed to lock bounty: " + e.getMessage(), e);
        }
        
        // Notify observers (Observer Pattern)
        notificationSubject.notifyObservers(session);
        
        return session;
    }
    
    /**
     * Accept a tutoring request.
     */
    @Transactional
    public TutoringSession acceptRequest(Long sessionId, Long tutorId) {
        
        TutoringSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        User tutor = userRepository.findById(tutorId)
            .orElseThrow(() -> new IllegalArgumentException("Tutor not found"));
        
        // State transition (State Pattern)
        try {
            stateContext.acceptSession(session, tutorId);
        } catch (InvalidStateTransitionException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        
        session.setTutor(tutor);
        session.setAcceptanceTime(LocalDateTime.now());
        
        return sessionRepository.save(session);
    }
    
    /**
     * Start a tutoring session.
     */
    @Transactional
    public TutoringSession startSession(Long sessionId) {
        TutoringSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        try {
            stateContext.startSession(session);
        } catch (InvalidStateTransitionException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        
        return sessionRepository.save(session);
    }
    
    /**
     * Complete a tutoring session.
     */
    @Transactional
    public TutoringSession completeSession(Long sessionId, Integer durationMinutes) {
        TutoringSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        try {
            stateContext.completeSession(session);
        } catch (InvalidStateTransitionException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        
        session.setDurationMinutes(durationMinutes);
        session.setCompletionTime(LocalDateTime.now());
        
        return sessionRepository.save(session);
    }
    
    /**
     * Raise a dispute for a session.
     */
    @Transactional
    public TutoringSession disputeSession(Long sessionId) {
        TutoringSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        try {
            stateContext.disputeSession(session);
        } catch (InvalidStateTransitionException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        
        return sessionRepository.save(session);
    }
    
    /**
     * Get all open sessions (requests).
     */
    public List<TutoringSession> getOpenSessions() {
        return sessionRepository.findByState(SessionState.OPEN);
    }
    
    /**
     * Get sessions for a student.
     */
    public List<TutoringSession> getStudentSessions(Long studentId) {
        return sessionRepository.findByStudentId(studentId);
    }
    
    /**
     * Get sessions for a tutor.
     */
    public List<TutoringSession> getTutorSessions(Long tutorId) {
        return sessionRepository.findByTutorId(tutorId);
    }
    
    /**
     * Get sessions by skill topic.
     */
    public List<TutoringSession> getSessionsBySkill(String skillTopic) {
        return sessionRepository.findBySkillTopic(skillTopic);
    }
    
    public TutoringSessionDTO toDTO(TutoringSession session) {
        UserService userService = new UserService(userRepository, walletRepository, null);
        return TutoringSessionDTO.builder()
            .id(session.getId())
            .student(session.getStudent() != null ? userService.toDTO(session.getStudent()) : null)
            .tutor(session.getTutor() != null ? userService.toDTO(session.getTutor()) : null)
            .title(session.getTitle())
            .description(session.getDescription())
            .skillTopic(session.getSkillTopic())
            .bountyPoints(session.getBountyPoints())
            .state(session.getState().toString())
            .creationTime(session.getCreationTime())
            .acceptanceTime(session.getAcceptanceTime())
            .startTime(session.getStartTime())
            .completionTime(session.getCompletionTime())
            .durationMinutes(session.getDurationMinutes())
            .rating(session.getRating() != null ? session.getRating().toString() : null)
            .feedback(session.getFeedback())
            .createdAt(session.getCreatedAt())
            .build();
    }
}
