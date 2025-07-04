package com.chatappbackend.dto.chatmessage;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
  private String content;

  private UUID chatRoomId;

  private String userId;
}
