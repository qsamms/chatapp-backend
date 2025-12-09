package com.chatappbackend.document;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageDocument {
  private Long user;
  private String room;
  private String text;
  private Instant timestamp;

  public String getTimestamp() {
    return timestamp.toString();
  }
}
