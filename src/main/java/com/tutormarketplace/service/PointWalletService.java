package com.tutormarketplace.service;

import com.tutormarketplace.dto.PointWalletDTO;
import com.tutormarketplace.model.PointLedger;
import com.tutormarketplace.model.PointWallet;
import com.tutormarketplace.model.TransactionType;
import com.tutormarketplace.repository.PointLedgerRepository;
import com.tutormarketplace.repository.PointWalletRepository;
import com.tutormarketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointWalletService {
    
    private final PointWalletRepository walletRepository;
    private final PointLedgerRepository ledgerRepository;
    private final UserRepository userRepository;
    
    /**
     * Get wallet for a user.
     */
    public PointWallet getWallet(Long userId) {
        return walletRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user: " + userId));
    }
    
    /**
     * Add initial points to a wallet (administrative operation).
     */
    @Transactional
    public PointWallet addInitialPoints(Long userId, Long amount) {
        PointWallet wallet = getWallet(userId);
        
        wallet.setAvailableBalance(wallet.getAvailableBalance() + amount);
        wallet.setTotalEarned(wallet.getTotalEarned() + amount);
        wallet = walletRepository.save(wallet);
        
        // Record in ledger
        recordTransaction(userId, TransactionType.DEPOSIT, amount, 
            "Initial points grant", wallet.getAvailableBalance());
        
        return wallet;
    }
    
    /**
     * Check if user has enough balance.
     */
    public boolean hasEnoughBalance(Long userId, Long amount) {
        PointWallet wallet = getWallet(userId);
        return wallet.canWithdraw(amount);
    }
    
    /**
     * Get transaction history for a user.
     */
    public List<PointLedger> getTransactionHistory(Long userId) {
        return ledgerRepository.findByUserId(userId);
    }
    
    /**
     * Get transaction history for a specific period.
     */
    public List<PointLedger> getTransactionHistory(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return ledgerRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }
    
    /**
     * Record a transaction in the ledger.
     */
    @Transactional
    protected PointLedger recordTransaction(Long userId, TransactionType type, Long amount, 
                                           String description, Long balanceAfter) {
        PointLedger ledger = PointLedger.builder()
            .user(userRepository.findById(userId).orElseThrow())
            .transactionType(type)
            .amount(amount)
            .description(description)
            .balanceAfter(balanceAfter)
            .build();
        
        return ledgerRepository.save(ledger);
    }
    
    public PointWalletDTO toDTO(PointWallet wallet) {
        return PointWalletDTO.builder()
            .id(wallet.getId())
            .userId(wallet.getUser().getId())
            .availableBalance(wallet.getAvailableBalance())
            .lockedBalance(wallet.getLockedBalance())
            .totalEarned(wallet.getTotalEarned())
            .totalSpent(wallet.getTotalSpent())
            .build();
    }
}
