package com.chatappbackend.dto.message;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MessageBetween {
  private LocalDateTime start;

  private LocalDateTime end;
}
