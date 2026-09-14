package com.tutormarketplace.pattern.strategy;

import com.tutormarketplace.model.TutoringSession;

/**
 * Time-Based Strategy: Points are calculated based on session duration and a rate per minute.
 * Formula: points = durationMinutes * pointsPerMinute
 */
public class TimeBasedStrategy implements PointCalculationStrategy {
    
    private static final long DEFAULT_POINTS_PER_MINUTE = 1L;
    private static final long MIN_SESSION_DURATION = 15; // 15 minutes minimum
    
    @Override
    public Long calculatePoints(TutoringSession session) {
        if (!validate(session)) {
            return 0L;
        }
        
        Integer duration = session.getDurationMinutes();
        if (duration == null || duration < 0) {
            return 0L;
        }
        
        return duration * DEFAULT_POINTS_PER_MINUTE;
    }
    
    @Override
    public boolean validate(TutoringSession session) {
        // Must have duration specified and at least 15 minutes
        return session.getDurationMinutes() != null 
            && session.getDurationMinutes() >= MIN_SESSION_DURATION;
    }
    
    @Override
    public String getStrategyName() {
        return "TIME_BASED";
    }
    
    /**
     * Get the rate used by this strategy.
     * 
     * @return points per minute
     */
    public long getPointsPerMinute() {
        return DEFAULT_POINTS_PER_MINUTE;
    }
    
    /**
     * Get the minimum session duration.
     * 
     * @return minimum duration in minutes
     */
    public long getMinimumDuration() {
        return MIN_SESSION_DURATION;
    }
}
