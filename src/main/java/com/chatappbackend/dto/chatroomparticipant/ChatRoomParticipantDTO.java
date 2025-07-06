package com.chatappbackend.dto.chatroomparticipant;

import com.chatappbackend.models.ChatRoomParticipant;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomParticipantDTO {
  private UUID id;

  private String username;

  private UUID chatRoomId;

  private LocalDateTime joinedAt;

  private boolean hasAccepted;

  public ChatRoomParticipantDTO(ChatRoomParticipant chatRoomParticipant) {
    this.id = chatRoomParticipant.getId();
    this.username = chatRoomParticipant.getUser().getUsername();
    this.chatRoomId = chatRoomParticipant.getChatRoom().getId();
    this.joinedAt = chatRoomParticipant.getJoinedAt();
    this.hasAccepted = chatRoomParticipant.getHasAccepted();
  }
}
