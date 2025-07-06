package com.chatappbackend.dto.wsmessage;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomingWSChatMessage {
  private String content;

  private UUID chatRoomId;
}
