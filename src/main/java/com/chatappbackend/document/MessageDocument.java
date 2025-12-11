package com.chatappbackend.document;

import com.chatappbackend.models.Message;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Setter
@Builder
public class MessageDocument {
  @Getter private Long user;
  @Getter private String room;
  @Getter private String text;
  @Getter private String messageId;

  @Getter(AccessLevel.NONE)
  private Instant timestamp;

  public String getTimestamp() {
    return timestamp.toString();
  }

  public MessageDocument(Long user, String room, String text, String messageId, Instant timestamp) {
    this.user = user;
    this.room = room;
    this.text = text;
    this.messageId = messageId;
    this.timestamp = timestamp;
  }

  public MessageDocument(Message message) {
    this.user = message.getSender().getId();
    this.room = message.getChatRoom().getId().toString();
    this.text = message.getContent();
    this.messageId = message.getId().toString();
    this.timestamp = message.getTimestamp();
  }
}
