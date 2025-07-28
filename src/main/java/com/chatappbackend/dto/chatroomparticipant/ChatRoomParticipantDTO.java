package com.chatappbackend.dto.chatroomparticipant;

import com.chatappbackend.dto.user.UserDTO;
import com.chatappbackend.models.ChatRoomParticipant;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomParticipantDTO {
  private UserDTO user;

  private UUID chatRoomId;

  private LocalDateTime joinedAt;

  private boolean hasAccepted;

  public ChatRoomParticipantDTO(ChatRoomParticipant chatRoomParticipant) {
    this.user = new UserDTO(chatRoomParticipant.getUser());
    this.chatRoomId = chatRoomParticipant.getChatRoom().getId();
    this.joinedAt = chatRoomParticipant.getJoinedAt();
    this.hasAccepted = chatRoomParticipant.getHasAccepted();
  }
}
