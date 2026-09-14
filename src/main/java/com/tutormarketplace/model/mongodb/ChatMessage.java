package com.tutormarketplace.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    
    private Long senderId;
    
    private String senderName;
    
    private String content;
    
    private LocalDateTime sentAt;
}
