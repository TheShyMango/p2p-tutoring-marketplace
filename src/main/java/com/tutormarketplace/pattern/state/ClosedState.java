package com.tutormarketplace.pattern.state;

import com.tutormarketplace.model.TutoringSession;
import com.tutormarketplace.model.SessionState;

public class ClosedState implements com.tutormarketplace.pattern.state.SessionState {
    
    @Override
    public void accept(TutoringSession session, Long tutorId) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException(
            "Cannot accept session that is CLOSED. Session is already completed."
        );
    }
    
    @Override
    public void start(TutoringSession session) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot start session in CLOSED state.");
    }
    
    @Override
    public void complete(TutoringSession session) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException(
            "Cannot complete session that is already CLOSED."
        );
    }
    
    @Override
    public void dispute(TutoringSession session) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException(
            "Cannot dispute session that is already CLOSED. Dispute period expired."
        );
    }
    
    @Override
    public void resolve(TutoringSession session) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot resolve dispute in CLOSED state.");
    }
    
    @Override
    public String getStateName() {
        return "CLOSED";
    }
}
