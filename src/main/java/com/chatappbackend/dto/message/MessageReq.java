package com.chatappbackend.dto.message;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MessageReq {
  private LocalDateTime after;

  private LocalDateTime before;
}
