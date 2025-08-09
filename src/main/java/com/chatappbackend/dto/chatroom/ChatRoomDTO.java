package com.chatappbackend.dto.chatroom;

import com.chatappbackend.dto.user.UserDTO;
import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.ChatRoomParticipant;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ChatRoomDTO {
  private UUID id;

  private String name;

  private String createdBy;

  private boolean isDm;

  private Instant createdAt;

  private List<UserDTO> participants;

  public ChatRoomDTO(ChatRoom chatRoom) {
    this.id = chatRoom.getId();
    this.name = chatRoom.getName();
    this.createdAt = chatRoom.getCreatedAt();
    this.createdBy = chatRoom.getCreatedBy().getUsername();
    this.isDm = chatRoom.isDm();
    this.participants =
        chatRoom.getParticipants().stream()
            .filter(ChatRoomParticipant::getHasAccepted)
            .map(participant -> new UserDTO(participant.getUser()))
            .toList();
  }
}
