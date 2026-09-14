package com.tutormarketplace.model;

public enum SessionState {
    OPEN,           // Request posted, awaiting acceptance
    IN_PROGRESS,    // Tutor accepted, session is ongoing
    IN_DISPUTE,     // Session completed but under dispute
    CLOSED          // Session completed successfully
}
