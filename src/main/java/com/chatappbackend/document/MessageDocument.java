package com.chatappbackend.document;

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
}
