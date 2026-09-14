package com.tutormarketplace.pattern.command;

/**
 * Command interface for encapsulating point transfer operations.
 * Supports both execute (forward) and undo (reverse) operations.
 */
public interface Command {
    
    /**
     * Execute the command (transfer points, lock escrow, etc.).
     * 
     * @throws CommandExecutionException if execution fails
     */
    void execute() throws CommandExecutionException;
    
    /**
     * Undo the command (reverse transfer, release escrow, etc.).
     * Typically used for dispute resolution.
     * 
     * @throws CommandExecutionException if undo fails
     */
    void undo() throws CommandExecutionException;
    
    /**
     * Get a description of the command for audit trails.
     * 
     * @return description
     */
    String getDescription();
}
