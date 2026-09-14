package com.tutormarketplace.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "chat_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLog {
    
    @Id
    private String id;
    
    private Long tutoringSessionId;
    
    private Long studentId;
    
    private Long tutorId;
    
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    public void addMessage(ChatMessage message) {
        messages.add(message);
        updatedAt = LocalDateTime.now();
    }
}
