package com.tutormarketplace.repository;

import com.tutormarketplace.model.PointLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointLedgerRepository extends JpaRepository<PointLedger, Long> {
    List<PointLedger> findByUserId(Long userId);
    List<PointLedger> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    List<PointLedger> findByRelatedSessionId(Long sessionId);
}
