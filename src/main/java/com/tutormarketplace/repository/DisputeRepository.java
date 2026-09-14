package com.tutormarketplace.repository;

import com.tutormarketplace.model.Dispute;
import com.tutormarketplace.model.DisputeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    List<Dispute> findByStatus(DisputeStatus status);
    List<Dispute> findByTutoringSessionId(Long sessionId);
    List<Dispute> findByRaisedById(Long userId);
}
