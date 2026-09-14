package com.tutormarketplace.pattern.state;

import com.tutormarketplace.model.TutoringSession;
import com.tutormarketplace.model.SessionState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SessionStateContext {
    
    private static final Map<SessionState, com.tutormarketplace.pattern.state.SessionState> stateMap 
        = new HashMap<>();
    
    public SessionStateContext() {
        stateMap.put(SessionState.OPEN, new OpenState());
        stateMap.put(SessionState.IN_PROGRESS, new InProgressState());
        stateMap.put(SessionState.IN_DISPUTE, new InDisputeState());
        stateMap.put(SessionState.CLOSED, new ClosedState());
    }
    
    public void acceptSession(TutoringSession session, Long tutorId) throws InvalidStateTransitionException {
        com.tutormarketplace.pattern.state.SessionState state = getState(session.getState());
        state.accept(session, tutorId);
    }
    
    public void startSession(TutoringSession session) throws InvalidStateTransitionException {
        com.tutormarketplace.pattern.state.SessionState state = getState(session.getState());
        state.start(session);
    }
    
    public void completeSession(TutoringSession session) throws InvalidStateTransitionException {
        com.tutormarketplace.pattern.state.SessionState state = getState(session.getState());
        state.complete(session);
    }
    
    public void disputeSession(TutoringSession session) throws InvalidStateTransitionException {
        com.tutormarketplace.pattern.state.SessionState state = getState(session.getState());
        state.dispute(session);
    }
    
    public void resolveDispute(TutoringSession session) throws InvalidStateTransitionException {
        com.tutormarketplace.pattern.state.SessionState state = getState(session.getState());
        state.resolve(session);
    }
    
    private com.tutormarketplace.pattern.state.SessionState getState(SessionState sessionState) {
        return stateMap.getOrDefault(sessionState, new ClosedState());
    }
}
