package com.tutormarketplace.model;

public enum EscrowState {
    LOCKED,    // Points are locked in escrow
    RELEASED,  // Points released to tutor after session completion
    REVERSED   // Points reversed back to student (dispute)
}
