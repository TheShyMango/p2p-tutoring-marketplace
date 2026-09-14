package com.tutormarketplace.pattern.facade;

import com.tutormarketplace.model.*;
import com.tutormarketplace.model.mongodb.SessionNote;
import com.tutormarketplace.pattern.command.CommandExecutionException;
import com.tutormarketplace.pattern.command.CommandInvoker;
import com.tutormarketplace.pattern.command.TransferPointsCommand;
import com.tutormarketplace.pattern.state.SessionStateContext;
import com.tutormarketplace.repository.*;
import com.tutormarketplace.repository.mongodb.SessionNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Facade for orchestrating the complex multi-step process of session completion and settlement.
 * Coordinates:
 * 1. State transition (IN_PROGRESS -> CLOSED)
 * 2. Point transfer execution (lock escrow, release to tutor)
 * 3. MongoDB document persistence (session notes, chat logs)
 * 4. Notification dispatch
 * 5. Ledger recording (immutable audit trail)
 * 
 * This simplifies complex business logic for callers and ensures atomicity.
 */
@Service
@RequiredArgsConstructor
public class SessionSettlementFacade {
    
    private final TutoringSessionRepository sessionRepository;
    private final PointWalletRepository walletRepository;
    private final EscrowLockRepository escrowRepository;
    private final PointLedgerRepository ledgerRepository;
    private final NotificationRepository notificationRepository;
    private final SessionNoteRepository sessionNoteRepository;
    
    private final SessionStateContext stateContext;
    private final CommandInvoker commandInvoker;
    
    /**
     * Complete a tutoring session and settle all associated operations atomically.
     * 
     * @param sessionId the session to complete
     * @param sessionNote optional MongoDB session note with review details
     * @throws SessionSettlementException if settlement fails
     */
    @Transactional
    public void settleCompletedSession(Long sessionId, SessionNote sessionNote) 
            throws SessionSettlementException {
        
        TutoringSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new SessionSettlementException("Session not found: " + sessionId));
        
        try {
            // Step 1: Validate current state
            if (!session.isInProgress()) {
                throw new SessionSettlementException(
                    "Session must be IN_PROGRESS to settle. Current state: " + session.getState()
                );
            }
            
            // Step 2: Transition state to CLOSED
            stateContext.completeSession(session);
            sessionRepository.save(session);
            
            // Step 3: Execute point transfer (escrow release to tutor)
            Long bountyPoints = session.getBountyPoints();
            User tutor = session.getTutor();
            
            if (tutor == null) {
                throw new SessionSettlementException("Session has no accepted tutor");
            }
            
            TransferPointsCommand releaseCommand = TransferPointsCommand.builder()
                .studentId(session.getStudent().getId())
                .tutorId(tutor.getId())
                .amount(bountyPoints)
                .tutoringSession(session)
                .transferType(TransferPointsCommand.TransferType.RELEASE_TO_TUTOR)
                .walletRepository(walletRepository)
                .ledgerRepository(ledgerRepository)
                .escrowRepository(escrowRepository)
                .build();
            
            commandInvoker.execute(releaseCommand);
            
            // Step 4: Persist session note to MongoDB (if provided)
            if (sessionNote != null) {
                sessionNote.setTutoringSessionId(sessionId);
                sessionNote.setStudentId(session.getStudent().getId());
                sessionNote.setTutorId(tutor.getId());
                sessionNote.setCreatedAt(LocalDateTime.now());
                sessionNoteRepository.save(sessionNote);
            }
            
            // Step 5: Dispatch notifications
            notifySessionCompletion(session);
            notifyPointsEarned(session, tutor, bountyPoints);
            notifyPointsDeducted(session, session.getStudent(), bountyPoints);
            
        } catch (Exception e) {
            throw new SessionSettlementException("Session settlement failed for ID: " + sessionId, e);
        }
    }
    
    /**
     * Handle dispute resolution and execute point reversal if needed.
     * 
     * @param sessionId the disputed session
     * @param awardPoints true to award full bounty to tutor, false to refund to student
     * @throws SessionSettlementException if resolution fails
     */
    @Transactional
    public void resolveDispute(Long sessionId, boolean awardPoints) 
            throws SessionSettlementException {
        
        TutoringSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new SessionSettlementException("Session not found: " + sessionId));
        
        try {
            if (!session.isInDispute()) {
                throw new SessionSettlementException(
                    "Session must be IN_DISPUTE to resolve. Current state: " + session.getState()
                );
            }
            
            Long bountyPoints = session.getBountyPoints();
            User student = session.getStudent();
            User tutor = session.getTutor();
            
            if (awardPoints) {
                // Award full bounty to tutor (execute escrow release)
                TransferPointsCommand releaseCommand = TransferPointsCommand.builder()
                    .studentId(student.getId())
                    .tutorId(tutor.getId())
                    .amount(bountyPoints)
                    .tutoringSession(session)
                    .transferType(TransferPointsCommand.TransferType.RELEASE_TO_TUTOR)
                    .walletRepository(walletRepository)
                    .ledgerRepository(ledgerRepository)
                    .escrowRepository(escrowRepository)
                    .build();
                
                commandInvoker.execute(releaseCommand);
                notifyPointsEarned(session, tutor, bountyPoints);
            } else {
                // Refund bounty to student (reverse escrow)
                TransferPointsCommand refundCommand = TransferPointsCommand.builder()
                    .studentId(student.getId())
                    .tutorId(tutor.getId())
                    .amount(bountyPoints)
                    .tutoringSession(session)
                    .transferType(TransferPointsCommand.TransferType.REFUND_TO_STUDENT)
                    .walletRepository(walletRepository)
                    .ledgerRepository(ledgerRepository)
                    .escrowRepository(escrowRepository)
                    .build();
                
                commandInvoker.execute(refundCommand);
                notifyPointsRefunded(session, student, bountyPoints);
            }
            
            // Transition to CLOSED
            stateContext.resolveDispute(session);
            sessionRepository.save(session);
            
            // Notify dispute resolution
            notifyDisputeResolved(session, awardPoints);
            
        } catch (Exception e) {
            throw new SessionSettlementException("Dispute resolution failed for ID: " + sessionId, e);
        }
    }
    
    // Notification helper methods
    private void notifySessionCompletion(TutoringSession session) {
        Notification notification = Notification.builder()
            .user(session.getStudent())
            .notificationType(NotificationType.SESSION_COMPLETED)
            .title("Session Completed")
            .message("Your tutoring session with " + session.getTutor().getFullName() + " is complete.")
            .relatedSession(session)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
    }
    
    private void notifyPointsEarned(TutoringSession session, User tutor, Long points) {
        Notification notification = Notification.builder()
            .user(tutor)
            .notificationType(NotificationType.POINTS_RECEIVED)
            .title("Points Earned")
            .message("You earned " + points + " points for completing your tutoring session.")
            .relatedSession(session)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
    }
    
    private void notifyPointsDeducted(TutoringSession session, User student, Long points) {
        Notification notification = Notification.builder()
            .user(student)
            .notificationType(NotificationType.POINTS_DEDUCTED)
            .title("Points Deducted")
            .message("You were charged " + points + " points for the completed tutoring session.")
            .relatedSession(session)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
    }
    
    private void notifyPointsRefunded(TutoringSession session, User student, Long points) {
        Notification notification = Notification.builder()
            .user(student)
            .notificationType(NotificationType.POINTS_RECEIVED)
            .title("Points Refunded")
            .message("You were refunded " + points + " points due to dispute resolution.")
            .relatedSession(session)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
    }
    
    private void notifyDisputeResolved(TutoringSession session, boolean awardedToTutor) {
        String message = awardedToTutor 
            ? "Dispute resolved in favor of the tutor. Full bounty awarded."
            : "Dispute resolved in favor of the student. Bounty refunded.";
        
        Notification notificationStudent = Notification.builder()
            .user(session.getStudent())
            .notificationType(NotificationType.DISPUTE_RESOLVED)
            .title("Dispute Resolved")
            .message(message)
            .relatedSession(session)
            .createdAt(LocalDateTime.now())
            .build();
        
        Notification notificationTutor = Notification.builder()
            .user(session.getTutor())
            .notificationType(NotificationType.DISPUTE_RESOLVED)
            .title("Dispute Resolved")
            .message(message)
            .relatedSession(session)
            .createdAt(LocalDateTime.now())
            .build();
        
        notificationRepository.save(notificationStudent);
        notificationRepository.save(notificationTutor);
    }
}
