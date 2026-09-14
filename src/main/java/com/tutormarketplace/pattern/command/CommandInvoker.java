package com.tutormarketplace.pattern.command;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Invoker pattern implementation for managing command execution and undo history.
 * Maintains a history of executed commands for potential reversal.
 */
@Component
public class CommandInvoker {
    
    private final Deque<Command> commandHistory = new LinkedList<>();
    
    /**
     * Execute a command and add it to history.
     * 
     * @param command the command to execute
     * @throws CommandExecutionException if execution fails
     */
    public void execute(Command command) throws CommandExecutionException {
        command.execute();
        commandHistory.push(command);
    }
    
    /**
     * Undo the last executed command.
     * 
     * @throws CommandExecutionException if undo fails or no commands in history
     */
    public void undo() throws CommandExecutionException {
        if (commandHistory.isEmpty()) {
            throw new CommandExecutionException("No commands to undo");
        }
        
        Command command = commandHistory.pop();
        command.undo();
    }
    
    /**
     * Undo the last N commands.
     * 
     * @param count number of commands to undo
     * @throws CommandExecutionException if undo fails
     */
    public void undo(int count) throws CommandExecutionException {
        for (int i = 0; i < count && !commandHistory.isEmpty(); i++) {
            undo();
        }
    }
    
    /**
     * Get the history of executed commands.
     * 
     * @return immutable copy of command history
     */
    public List<Command> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }
    
    /**
     * Clear all command history.
     */
    public void clearHistory() {
        commandHistory.clear();
    }
    
    /**
     * Get the size of command history.
     * 
     * @return number of commands in history
     */
    public int getHistorySize() {
        return commandHistory.size();
    }
}
