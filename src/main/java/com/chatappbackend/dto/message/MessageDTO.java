package com.chatappbackend.dto.message;

import com.chatappbackend.models.Message;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO {
  private UUID id;

  private String content;

  private String sender;

  private UUID chatRoomId;

  private LocalDateTime timestamp;

  private String mediaUrl;

  public MessageDTO(Message message) {
    this.id = message.getId();
    this.content = message.getContent();
    this.sender = message.getSender().getUsername();
    this.chatRoomId = message.getChatRoom().getId();
    this.timestamp = message.getTimestamp();
    this.mediaUrl = message.getMediaUrl();
  }
}
