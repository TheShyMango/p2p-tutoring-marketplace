package com.tutormarketplace.model;

public enum DisputeStatus {
    OPEN,                 // Dispute is under review
    RESOLVED,             // General resolved state
    AWARDED_TO_TUTOR,     // Dispute resolved in favor of the tutor
    REFUNDED_TO_STUDENT   // Dispute resolved in favor of the student
}