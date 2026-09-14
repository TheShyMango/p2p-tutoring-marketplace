package com.tutormarketplace.repository;

import com.tutormarketplace.model.EscrowLock;
import com.tutormarketplace.model.EscrowState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EscrowLockRepository extends JpaRepository<EscrowLock, Long> {
    Optional<EscrowLock> findByTutoringSessionId(Long sessionId);
    List<EscrowLock> findByState(EscrowState state);
    List<EscrowLock> findByStudentId(Long studentId);
    List<EscrowLock> findByTutorId(Long tutorId);
}
