package com.tutormarketplace.model;

public enum TransactionType {
    DEPOSIT,           // Initial points added
    BOUNTY_POSTED,     // Points locked when posting bounty
    EARNING,           // Points earned by tutor
    SPENDING,          // Points spent by student
    REFUND,            // Points refunded (dispute resolution)
    REVERSAL           // Points reversed (undo)
}
