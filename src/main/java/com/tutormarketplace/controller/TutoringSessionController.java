package com.tutormarketplace.controller;

import com.tutormarketplace.dto.PostTutoringRequestRequest;
import com.tutormarketplace.dto.TutoringSessionDTO;
import com.tutormarketplace.model.TutoringSession;
import com.tutormarketplace.model.User;
import com.tutormarketplace.pattern.facade.SessionSettlementFacade;
import com.tutormarketplace.service.TutoringSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "Tutoring Sessions", description = "Manage tutoring requests, sessions, and completion")
public class TutoringSessionController {
    
    private final TutoringSessionService sessionService;
    private final SessionSettlementFacade settlementFacade;
    
    @PostMapping
    @Operation(summary = "Post a new tutoring request", description = "Create a new tutoring request (bounty)")
    public ResponseEntity<TutoringSessionDTO> postRequest(
            Authentication auth,
            @Valid @RequestBody PostTutoringRequestRequest request) {
        
        User student = (User) auth.getPrincipal();
        
        TutoringSession session = sessionService.postRequest(
            student.getId(),
            request.getTitle(),
            request.getDescription(),
            request.getSkillTopic(),
            request.getBountyPoints()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(sessionService.toDTO(session));
    }
    
    @GetMapping
    @Operation(summary = "Get all open requests", description = "Retrieve all open tutoring requests")
    public ResponseEntity<List<TutoringSessionDTO>> getOpenRequests() {
        // FIXED: Now calling the Service method that handles the transaction and mapping
        List<TutoringSessionDTO> sessions = sessionService.getOpenSessionsAsDTOs();
        return ResponseEntity.ok(sessions);
    }
    
    @GetMapping("/skill/{skillTopic}")
    @Operation(summary = "Get requests by skill", description = "Filter tutoring requests by skill topic")
    public ResponseEntity<List<TutoringSessionDTO>> getRequestsBySkill(@PathVariable String skillTopic) {
        // FIXED: Now calling the Service method that handles the transaction and mapping
        List<TutoringSessionDTO> sessions = sessionService.getSessionsBySkillAsDTOs(skillTopic);
        return ResponseEntity.ok(sessions);
    }
    
    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student sessions", description = "Retrieve all sessions for a student")
    public ResponseEntity<List<TutoringSessionDTO>> getStudentSessions(@PathVariable Long studentId) {
        // FIXED: Now calling the Service method that handles the transaction and mapping
        List<TutoringSessionDTO> sessions = sessionService.getStudentSessionsAsDTOs(studentId);
        return ResponseEntity.ok(sessions);
    }
    
    @GetMapping("/tutor/{tutorId}")
    @Operation(summary = "Get tutor sessions", description = "Retrieve all sessions for a tutor")
    public ResponseEntity<List<TutoringSessionDTO>> getTutorSessions(@PathVariable Long tutorId) {
        // FIXED: Now calling the Service method that handles the transaction and mapping
        List<TutoringSessionDTO> sessions = sessionService.getTutorSessionsAsDTOs(tutorId);
        return ResponseEntity.ok(sessions);
    }
    
    @PostMapping("/{sessionId}/accept")
    @Operation(summary = "Accept a tutoring request", description = "Accept a request and become the tutor")
    public ResponseEntity<TutoringSessionDTO> acceptRequest(
            @PathVariable Long sessionId,
            Authentication auth) {
        
        User tutor = (User) auth.getPrincipal();
        
        TutoringSession session = sessionService.acceptRequest(sessionId, tutor.getId());
        return ResponseEntity.ok(sessionService.toDTO(session));
    }
    
    @PostMapping("/{sessionId}/start")
    @Operation(summary = "Start a session", description = "Mark a session as started")
    public ResponseEntity<TutoringSessionDTO> startSession(@PathVariable Long sessionId) {
        TutoringSession session = sessionService.startSession(sessionId);
        return ResponseEntity.ok(sessionService.toDTO(session));
    }
    
    @PostMapping("/{sessionId}/complete")
    @Operation(summary = "Complete a session", description = "Mark a session as completed")
    public ResponseEntity<TutoringSessionDTO> completeSession(
            @PathVariable Long sessionId,
            @RequestParam(required = false, defaultValue = "60") Integer durationMinutes) {
        
        TutoringSession session = sessionService.completeSession(sessionId, durationMinutes);
        return ResponseEntity.ok(sessionService.toDTO(session));
    }
    
    @PostMapping("/{sessionId}/dispute")
    @Operation(summary = "Raise a dispute", description = "Raise a dispute for a session")
    public ResponseEntity<TutoringSessionDTO> raiseDispute(@PathVariable Long sessionId) {
        TutoringSession session = sessionService.disputeSession(sessionId);
        return ResponseEntity.ok(sessionService.toDTO(session));
    }
}