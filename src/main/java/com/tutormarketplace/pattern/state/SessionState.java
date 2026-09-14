package com.tutormarketplace.pattern.state;

import com.tutormarketplace.model.TutoringSession;

public interface SessionState {
    
    void accept(TutoringSession session, Long tutorId) throws InvalidStateTransitionException;
    
    void start(TutoringSession session) throws InvalidStateTransitionException;
    
    void complete(TutoringSession session) throws InvalidStateTransitionException;
    
    void dispute(TutoringSession session) throws InvalidStateTransitionException;
    
    void resolve(TutoringSession session) throws InvalidStateTransitionException;
    
    String getStateName();
}

