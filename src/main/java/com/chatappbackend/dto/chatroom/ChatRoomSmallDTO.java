package com.chatappbackend.dto.chatroom;

import com.chatappbackend.dto.user.UserDTO;
import com.chatappbackend.models.ChatRoom;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomSmallDTO {
  private UUID id;

  private String name;

  private String createdBy;

  private boolean isDm;

  private Instant createdAt;

  private List<UserDTO> participants;

  private UserDTO otherParticipant;

  public ChatRoomSmallDTO(ChatRoom chatRoom) {
    this.id = chatRoom.getId();
    this.name = chatRoom.getName();
    this.createdAt = chatRoom.getCreatedAt();
    this.createdBy = chatRoom.getCreatedBy().getUsername();
    this.isDm = chatRoom.isDm();
  }
}
