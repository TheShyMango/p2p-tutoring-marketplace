package com.tutormarketplace.controller;

import com.tutormarketplace.dto.PointWalletDTO;
import com.tutormarketplace.model.PointLedger;
import com.tutormarketplace.model.User;
import com.tutormarketplace.service.PointWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@Tag(name = "Point Wallet", description = "Manage points and wallet balance")
public class PointWalletController {
    
    private final PointWalletService walletService;
    
    @GetMapping("/me")
    @Operation(summary = "Get my wallet", description = "Retrieve current user's wallet balance")
    public ResponseEntity<PointWalletDTO> getMyWallet(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(
            walletService.toDTO(walletService.getWallet(user.getId()))
        );
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user wallet", description = "Retrieve a user's wallet balance")
    public ResponseEntity<PointWalletDTO> getUserWallet(@PathVariable Long userId) {
        return ResponseEntity.ok(
            walletService.toDTO(walletService.getWallet(userId))
        );
    }
    
    @PostMapping("/{userId}/add-points")
    @Operation(summary = "Add points", description = "Administrative operation to add initial points")
    public ResponseEntity<PointWalletDTO> addPoints(
            @PathVariable Long userId,
            @RequestParam Long amount) {
        
        if (amount <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(
            walletService.toDTO(walletService.addInitialPoints(userId, amount))
        );
    }
    
    @GetMapping("/me/transactions")
    @Operation(summary = "Get my transactions", description = "Retrieve current user's transaction history")
    public ResponseEntity<List<PointLedger>> getMyTransactionHistory(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(
            walletService.getTransactionHistory(user.getId())
        );
    }
    
    @GetMapping("/{userId}/transactions")
    @Operation(summary = "Get user transactions", description = "Retrieve a user's transaction history")
    public ResponseEntity<List<PointLedger>> getUserTransactionHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(
            walletService.getTransactionHistory(userId)
        );
    }
}
