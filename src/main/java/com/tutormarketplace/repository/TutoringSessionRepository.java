package com.tutormarketplace.repository;

import com.tutormarketplace.model.TutoringSession;
import com.tutormarketplace.model.SessionState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutoringSessionRepository extends JpaRepository<TutoringSession, Long> {
    List<TutoringSession> findByState(SessionState state);
    List<TutoringSession> findByStudentId(Long studentId);
    List<TutoringSession> findByTutorId(Long tutorId);
    List<TutoringSession> findBySkillTopic(String skillTopic);
}
