package com.chatappbackend.dto.wsmessage;

import com.chatappbackend.models.Message;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
public class OutgoingWSChatMessage {
  private String sender;

  private String content;

  private UUID chatRoomId;

  private UUID id;

  private LocalDateTime timestamp;

  private String mediaUrl;

  public OutgoingWSChatMessage(Message message) {
    this.sender = message.getSender().getUsername();
    this.content = message.getContent();
    this.chatRoomId = message.getChatRoom().getId();
    this.id = message.getId();
    this.timestamp = message.getTimestamp();
    this.mediaUrl = message.getMediaUrl();
  }
}
