package com.tutormarketplace.service;

import com.tutormarketplace.model.Dispute;
import com.tutormarketplace.model.DisputeStatus;
import com.tutormarketplace.model.TutoringSession;
import com.tutormarketplace.model.User;
import com.tutormarketplace.pattern.facade.SessionSettlementFacade;
import com.tutormarketplace.pattern.facade.SessionSettlementException;
import com.tutormarketplace.repository.DisputeRepository;
import com.tutormarketplace.repository.TutoringSessionRepository;
import com.tutormarketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisputeService {
    
    private final DisputeRepository disputeRepository;
    private final TutoringSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SessionSettlementFacade settlementFacade;
    
    /**
     * Raise a dispute for a session.
     */
    @Transactional
    public Dispute raiseDispute(Long sessionId, Long userId, String reason, String description) {
        TutoringSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Dispute dispute = Dispute.builder()
            .tutoringSession(session)
            .raisedBy(user)
            .status(DisputeStatus.OPEN)
            .reason(reason)
            .description(description)
            .build();
        
        return disputeRepository.save(dispute);
    }
    
    /**
     * Resolve a dispute by awarding points to tutor or refunding to student.
     */
    @Transactional
    public Dispute resolveDispute(Long disputeId, boolean awardToTutor) throws SessionSettlementException {
        Dispute dispute = disputeRepository.findById(disputeId)
            .orElseThrow(() -> new IllegalArgumentException("Dispute not found"));
        
        if (dispute.getStatus() != DisputeStatus.OPEN) {
            throw new IllegalArgumentException("Dispute is already resolved");
        }
        
        TutoringSession session = dispute.getTutoringSession();
        
        // Delegate to settlement facade for atomicity
        settlementFacade.resolveDispute(session.getId(), awardToTutor);
        
        // Mark dispute as resolved
        dispute.setStatus(awardToTutor ? DisputeStatus.AWARDED_TO_TUTOR : DisputeStatus.REFUNDED_TO_STUDENT);
        return disputeRepository.save(dispute);
    }
    
    /**
     * Get disputes by status.
     */
    public List<Dispute> getDisputesByStatus(DisputeStatus status) {
        return disputeRepository.findByStatus(status);
    }
    
    /**
     * Get all disputes for a session.
     */
    public Dispute getSessionDispute(Long sessionId) {
        return disputeRepository.findByTutoringSessionId(sessionId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No dispute for this session"));
    }
    
    /**
     * Get all disputes raised by a user.
     */
    public List<Dispute> getUserDisputes(Long userId) {
        return disputeRepository.findByRaisedById(userId);
    }
}