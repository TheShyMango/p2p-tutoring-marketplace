package com.tutormarketplace.pattern.strategy;

import com.tutormarketplace.model.TutoringSession;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Context for selecting and executing point calculation strategies at runtime.
 */
@Component
public class PointCalculationContext {
    
    private final Map<String, PointCalculationStrategy> strategies = new HashMap<>();
    
    public PointCalculationContext() {
        strategies.put("FIXED_BOUNTY", new FixedBountyStrategy());
        strategies.put("TIME_BASED", new TimeBasedStrategy());
    }
    
    /**
     * Calculate points using the specified strategy.
     * Defaults to FIXED_BOUNTY if strategy not found.
     * 
     * @param session the tutoring session
     * @param strategyName the strategy name
     * @return calculated points
     */
    public Long calculatePoints(TutoringSession session, String strategyName) {
        PointCalculationStrategy strategy = strategies.getOrDefault(
            strategyName, 
            strategies.get("FIXED_BOUNTY")
        );
        return strategy.calculatePoints(session);
    }
    
    /**
     * Validate calculation using the specified strategy.
     * 
     * @param session the tutoring session
     * @param strategyName the strategy name
     * @return true if valid, false otherwise
     */
    public boolean validateCalculation(TutoringSession session, String strategyName) {
        PointCalculationStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            return false;
        }
        return strategy.validate(session);
    }
    
    /**
     * Register a custom strategy at runtime.
     * 
     * @param strategyName unique strategy name
     * @param strategy the strategy implementation
     */
    public void registerStrategy(String strategyName, PointCalculationStrategy strategy) {
        strategies.put(strategyName, strategy);
    }
    
    /**
     * Get the strategy by name.
     * 
     * @param strategyName the strategy name
     * @return the strategy or null if not found
     */
    public PointCalculationStrategy getStrategy(String strategyName) {
        return strategies.get(strategyName);
    }
}
