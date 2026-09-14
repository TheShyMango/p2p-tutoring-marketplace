package com.tutormarketplace.pattern.state;

import com.tutormarketplace.model.TutoringSession;
import com.tutormarketplace.model.SessionState;
import java.time.LocalDateTime;

public class InProgressState implements com.tutormarketplace.pattern.state.SessionState {
    
    @Override
    public void accept(TutoringSession session, Long tutorId) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException(
            "Cannot accept session that is already IN_PROGRESS. Session already accepted."
        );
    }
    
    @Override
    public void start(TutoringSession session) throws InvalidStateTransitionException {
        if (session.getState() != SessionState.IN_PROGRESS) {
            throw new InvalidStateTransitionException(
                "Cannot start session not in IN_PROGRESS state. Current state: " + session.getState()
            );
        }
        session.setStartTime(LocalDateTime.now());
    }
    
    @Override
    public void complete(TutoringSession session) throws InvalidStateTransitionException {
        if (session.getState() != SessionState.IN_PROGRESS) {
            throw new InvalidStateTransitionException(
                "Cannot complete session not in IN_PROGRESS state. Current state: " + session.getState()
            );
        }
        session.setState(SessionState.CLOSED);
        session.setCompletionTime(LocalDateTime.now());
    }
    
    @Override
    public void dispute(TutoringSession session) throws InvalidStateTransitionException {
        if (session.getState() != SessionState.IN_PROGRESS) {
            throw new InvalidStateTransitionException(
                "Cannot dispute session not in IN_PROGRESS state. Current state: " + session.getState()
            );
        }
        session.setState(SessionState.IN_DISPUTE);
    }
    
    @Override
    public void resolve(TutoringSession session) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot resolve dispute in IN_PROGRESS state.");
    }
    
    @Override
    public String getStateName() {
        return "IN_PROGRESS";
    }
}
