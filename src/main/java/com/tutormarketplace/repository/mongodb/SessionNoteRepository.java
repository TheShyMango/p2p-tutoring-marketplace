package com.tutormarketplace.repository.mongodb;

import com.tutormarketplace.model.mongodb.SessionNote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionNoteRepository extends MongoRepository<SessionNote, String> {
    Optional<SessionNote> findByTutoringSessionId(Long sessionId);
    List<SessionNote> findByStudentId(Long studentId);
    List<SessionNote> findByTutorId(Long tutorId);
}
