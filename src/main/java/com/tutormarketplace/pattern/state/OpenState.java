package com.tutormarketplace.pattern.state;

import com.tutormarketplace.model.TutoringSession;
import com.tutormarketplace.model.SessionState;
import java.time.LocalDateTime;

public class OpenState implements com.tutormarketplace.pattern.state.SessionState {
    
    @Override
    public void accept(TutoringSession session, Long tutorId) throws InvalidStateTransitionException {
        if (session.getState() != SessionState.OPEN) {
            throw new InvalidStateTransitionException(
                "Cannot accept session that is not in OPEN state. Current state: " + session.getState()
            );
        }
        session.setState(SessionState.IN_PROGRESS);
    }
    
    @Override
    public void start(TutoringSession session) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot start session in OPEN state. Must accept first.");
    }
    
    @Override
    public void complete(TutoringSession session) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot complete session in OPEN state. Must accept first.");
    }
    
    @Override
    public void dispute(TutoringSession session) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot dispute session in OPEN state.");
    }
    
    @Override
    public void resolve(TutoringSession session) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot resolve dispute in OPEN state.");
    }
    
    @Override
    public String getStateName() {
        return "OPEN";
    }
}
