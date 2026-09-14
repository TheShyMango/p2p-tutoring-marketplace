package com.tutormarketplace.pattern.strategy;

import com.tutormarketplace.model.TutoringSession;

public interface PointCalculationStrategy {
    
    /**
     * Calculate the points to be charged for a tutoring session.
     * 
     * @param session the tutoring session
     * @return calculated points
     */
    Long calculatePoints(TutoringSession session);
    
    /**
     * Validate if the calculation is feasible.
     * 
     * @param session the tutoring session
     * @return true if valid, false otherwise
     */
    boolean validate(TutoringSession session);
    
    /**
     * Get the strategy name.
     * 
     * @return strategy name
     */
    String getStrategyName();
}
