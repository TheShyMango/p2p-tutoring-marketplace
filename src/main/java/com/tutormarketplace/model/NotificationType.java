package com.tutormarketplace.model;

public enum NotificationType {
    SKILL_MATCH,           // New request matching subscribed skill
    REQUEST_ACCEPTED,      // Your request was accepted
    SESSION_COMPLETED,     // Session completed and awaiting settlement
    POINTS_RECEIVED,       // You received points
    POINTS_DEDUCTED,       // Points were deducted
    DISPUTE_RAISED,        // A dispute was raised
    DISPUTE_RESOLVED       // Dispute was resolved
}
