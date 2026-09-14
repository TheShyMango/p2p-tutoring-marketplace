package com.tutormarketplace.repository.mongodb;

import com.tutormarketplace.model.mongodb.ChatLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatLogRepository extends MongoRepository<ChatLog, String> {
    Optional<ChatLog> findByTutoringSessionId(Long sessionId);
    List<ChatLog> findByStudentId(Long studentId);
    List<ChatLog> findByTutorId(Long tutorId);
}
