package com.tutormarketplace.pattern.state;

import com.tutormarketplace.model.TutoringSession;
import com.tutormarketplace.model.SessionState;

public class InDisputeState implements com.tutormarketplace.pattern.state.SessionState {
    
    @Override
    public void accept(TutoringSession session, Long tutorId) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException(
            "Cannot accept session that is IN_DISPUTE. Session is under review."
        );
    }
    
    @Override
    public void start(TutoringSession session) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot start session in IN_DISPUTE state.");
    }
    
    @Override
    public void complete(TutoringSession session) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException(
            "Cannot complete session in IN_DISPUTE state. Must resolve dispute first."
        );
    }
    
    @Override
    public void dispute(TutoringSession session) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException(
            "Session is already IN_DISPUTE. Cannot raise another dispute."
        );
    }
    
    @Override
    public void resolve(TutoringSession session) throws InvalidStateTransitionException {
        if (session.getState() != SessionState.IN_DISPUTE) {
            throw new InvalidStateTransitionException(
                "Cannot resolve dispute for session not in IN_DISPUTE state. Current state: " + session.getState()
            );
        }
        session.setState(SessionState.CLOSED);
    }
    
    @Override
    public String getStateName() {
        return "IN_DISPUTE";
    }
}
