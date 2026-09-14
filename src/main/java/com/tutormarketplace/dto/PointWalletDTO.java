package com.tutormarketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointWalletDTO {
    private Long id;
    private Long userId;
    private Long availableBalance;
    private Long lockedBalance;
    private Long totalEarned;
    private Long totalSpent;
}
