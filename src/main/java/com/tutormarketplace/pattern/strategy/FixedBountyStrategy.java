package com.tutormarketplace.pattern.strategy;

import com.tutormarketplace.model.TutoringSession;

/**
 * Fixed Bounty Strategy: The student sets a fixed point bounty for the entire session,
 * regardless of duration.
 */
public class FixedBountyStrategy implements PointCalculationStrategy {
    
    @Override
    public Long calculatePoints(TutoringSession session) {
        if (!validate(session)) {
            return 0L;
        }
        return session.getBountyPoints();
    }
    
    @Override
    public boolean validate(TutoringSession session) {
        // Bounty must be positive
        return session.getBountyPoints() != null && session.getBountyPoints() > 0;
    }
    
    @Override
    public String getStrategyName() {
        return "FIXED_BOUNTY";
    }
}
