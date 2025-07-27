package com.chatappbackend.dto.message;

import java.time.Instant;
import lombok.Getter;

@Getter
public class MessageReq {
  private Instant after;

  private Instant before;
}
