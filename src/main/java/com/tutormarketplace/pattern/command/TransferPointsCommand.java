package com.tutormarketplace.pattern.command;

import com.tutormarketplace.model.*;
import com.tutormarketplace.repository.EscrowLockRepository;
import com.tutormarketplace.repository.PointLedgerRepository;
import com.tutormarketplace.repository.PointWalletRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Concrete Command for transferring points with escrow management.
 * Locks points from student, optionally releases to tutor on completion.
 * Supports undo for dispute resolution.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferPointsCommand implements Command {
    
    private Long studentId;
    private Long tutorId;
    private Long amount;
    private TutoringSession tutoringSession;
    private TransferType transferType;
    
    // Repositories for persistence
    private PointWalletRepository walletRepository;
    private PointLedgerRepository ledgerRepository;
    private EscrowLockRepository escrowRepository;
    
    // Execution state for undo support
    private boolean executed = false;
    private PointWallet studentWalletBefore;
    private PointWallet tutorWalletBefore;
    private EscrowLock escrowLock;
    
    @Override
    public void execute() throws CommandExecutionException {
        if (executed) {
            throw new CommandExecutionException("Command has already been executed");
        }
        
        try {
            PointWallet studentWallet = walletRepository.findByUserId(studentId)
                .orElseThrow(() -> new CommandExecutionException("Student wallet not found"));
            
            PointWallet tutorWallet = walletRepository.findByUserId(tutorId)
                .orElseThrow(() -> new CommandExecutionException("Tutor wallet not found"));
            
            // Store state for undo
            studentWalletBefore = new PointWallet(
                studentWallet.getId(),
                studentWallet.getUser(),
                studentWallet.getAvailableBalance(),
                studentWallet.getLockedBalance(),
                studentWallet.getTotalEarned(),
                studentWallet.getTotalSpent(),
                studentWallet.getVersion(),
                studentWallet.getCreatedAt(),
                studentWallet.getUpdatedAt()
            );
            tutorWalletBefore = new PointWallet(
                tutorWallet.getId(),
                tutorWallet.getUser(),
                tutorWallet.getAvailableBalance(),
                tutorWallet.getLockedBalance(),
                tutorWallet.getTotalEarned(),
                tutorWallet.getTotalSpent(),
                tutorWallet.getVersion(),
                tutorWallet.getCreatedAt(),
                tutorWallet.getUpdatedAt()
            );
            
            switch (transferType) {
                case LOCK_BOUNTY:
                    lockBounty(studentWallet);
                    break;
                case RELEASE_TO_TUTOR:
                    releaseToTutor(studentWallet, tutorWallet);
                    break;
                case REFUND_TO_STUDENT:
                    refundToStudent(studentWallet);
                    break;
                case ADD_INITIAL_POINTS:
                    addInitialPoints(studentWallet);
                    break;
            }
            
            executed = true;
        } catch (Exception e) {
            throw new CommandExecutionException("Failed to execute transfer command", e);
        }
    }
    
    @Override
    public void undo() throws CommandExecutionException {
        if (!executed) {
            throw new CommandExecutionException("Command must be executed before undo");
        }
        
        try {
            PointWallet studentWallet = walletRepository.findByUserId(studentId)
                .orElseThrow(() -> new CommandExecutionException("Student wallet not found"));
            
            PointWallet tutorWallet = walletRepository.findByUserId(tutorId)
                .orElseThrow(() -> new CommandExecutionException("Tutor wallet not found"));
            
            switch (transferType) {
                case LOCK_BOUNTY:
                    unlockBounty(studentWallet);
                    break;
                case RELEASE_TO_TUTOR:
                    reverseRelease(studentWallet, tutorWallet);
                    break;
                case REFUND_TO_STUDENT:
                    reverseRefund(studentWallet);
                    break;
                case ADD_INITIAL_POINTS:
                    reverseInitialPoints(studentWallet);
                    break;
            }
            
            executed = false;
        } catch (Exception e) {
            throw new CommandExecutionException("Failed to undo transfer command", e);
        }
    }
    
    @Override
    public String getDescription() {
        return String.format(
            "Transfer %d points from student %d to tutor %d via %s for session %d",
            amount, studentId, tutorId, transferType, tutoringSession.getId()
        );
    }
    
    // Private helper methods
    private void lockBounty(PointWallet studentWallet) throws CommandExecutionException {
        if (!studentWallet.canWithdraw(amount)) {
            throw new CommandExecutionException(
                "Insufficient balance. Required: " + amount + ", Available: " + studentWallet.getAvailableBalance()
            );
        }
        
        studentWallet.setAvailableBalance(studentWallet.getAvailableBalance() - amount);
        studentWallet.setLockedBalance(studentWallet.getLockedBalance() + amount);
        walletRepository.save(studentWallet);
        
        // Create escrow lock
        escrowLock = EscrowLock.builder()
            .tutoringSession(tutoringSession)
            .student(studentWallet.getUser())
            .tutor(tutoringSession.getTutor())
            .lockedPoints(amount)
            .state(EscrowState.LOCKED)
            .build();
        escrowRepository.save(escrowLock);
        
        // Record in ledger
        recordTransaction(studentWallet, TransactionType.BOUNTY_POSTED, amount);
    }
    
    private void releaseToTutor(PointWallet studentWallet, PointWallet tutorWallet) 
            throws CommandExecutionException {
        if (!studentWallet.hasEnoughBalance(amount)) {
            throw new CommandExecutionException("Insufficient locked balance");
        }
        
        studentWallet.setLockedBalance(studentWallet.getLockedBalance() - amount);
        studentWallet.setTotalSpent(studentWallet.getTotalSpent() + amount);
        tutorWallet.setAvailableBalance(tutorWallet.getAvailableBalance() + amount);
        tutorWallet.setTotalEarned(tutorWallet.getTotalEarned() + amount);
        
        walletRepository.save(studentWallet);
        walletRepository.save(tutorWallet);
        
        // Update escrow
        if (escrowLock != null) {
            escrowLock.setState(EscrowState.RELEASED);
            escrowLock.setReleasedAt(LocalDateTime.now());
            escrowRepository.save(escrowLock);
        }
        
        // Record transactions
        recordTransaction(tutorWallet, TransactionType.EARNING, amount);
    }
    
    private void refundToStudent(PointWallet studentWallet) throws CommandExecutionException {
        studentWallet.setLockedBalance(studentWallet.getLockedBalance() - amount);
        studentWallet.setAvailableBalance(studentWallet.getAvailableBalance() + amount);
        walletRepository.save(studentWallet);
        
        // Update escrow
        if (escrowLock != null) {
            escrowLock.setState(EscrowState.REVERSED);
            escrowLock.setReversedAt(LocalDateTime.now());
            escrowLock.setReason("Dispute resolved - refund to student");
            escrowRepository.save(escrowLock);
        }
        
        recordTransaction(studentWallet, TransactionType.REFUND, amount);
    }
    
    private void addInitialPoints(PointWallet studentWallet) {
        studentWallet.setAvailableBalance(studentWallet.getAvailableBalance() + amount);
        studentWallet.setTotalEarned(studentWallet.getTotalEarned() + amount);
        walletRepository.save(studentWallet);
        recordTransaction(studentWallet, TransactionType.DEPOSIT, amount);
    }
    
    // Undo operations
    private void unlockBounty(PointWallet studentWallet) {
        studentWallet.setAvailableBalance(studentWallet.getAvailableBalance() + amount);
        studentWallet.setLockedBalance(studentWallet.getLockedBalance() - amount);
        walletRepository.save(studentWallet);
        recordTransaction(studentWallet, TransactionType.REVERSAL, amount);
    }
    
    private void reverseRelease(PointWallet studentWallet, PointWallet tutorWallet) {
        studentWallet.setLockedBalance(studentWallet.getLockedBalance() + amount);
        studentWallet.setTotalSpent(studentWallet.getTotalSpent() - amount);
        tutorWallet.setAvailableBalance(tutorWallet.getAvailableBalance() - amount);
        tutorWallet.setTotalEarned(tutorWallet.getTotalEarned() - amount);
        walletRepository.save(studentWallet);
        walletRepository.save(tutorWallet);
    }
    
    private void reverseRefund(PointWallet studentWallet) {
        studentWallet.setLockedBalance(studentWallet.getLockedBalance() + amount);
        studentWallet.setAvailableBalance(studentWallet.getAvailableBalance() - amount);
        walletRepository.save(studentWallet);
    }
    
    private void reverseInitialPoints(PointWallet studentWallet) {
        studentWallet.setAvailableBalance(studentWallet.getAvailableBalance() - amount);
        studentWallet.setTotalEarned(studentWallet.getTotalEarned() - amount);
        walletRepository.save(studentWallet);
    }
    
    private void recordTransaction(PointWallet wallet, TransactionType type, Long amountValue) {
        PointLedger ledger = PointLedger.builder()
            .user(wallet.getUser())
            .transactionType(type)
            .amount(amountValue)
            .relatedSession(tutoringSession)
            .balanceAfter(wallet.getAvailableBalance())
            .description(getDescription())
            .build();
        ledgerRepository.save(ledger);
    }
    
    public enum TransferType {
        LOCK_BOUNTY,
        RELEASE_TO_TUTOR,
        REFUND_TO_STUDENT,
        ADD_INITIAL_POINTS
    }
}
