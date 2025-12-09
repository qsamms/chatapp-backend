package com.chatappbackend.dto.chatroom;

import com.chatappbackend.dto.user.UserDTO;
import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.ChatRoomParticipant;
import com.chatappbackend.models.User;
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

  private UserDTO otherParticipant;

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
    this.otherParticipant = null;
  }

  public ChatRoomDTO(ChatRoom chatRoom, User reqUser) {
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
    this.otherParticipant =
        chatRoom.getParticipants().stream()
            .map(ChatRoomParticipant::getUser)
            .filter(user -> !user.getUsername().equals(reqUser.getUsername()))
            .findFirst()
            .map(UserDTO::new)
            .orElse(null);
  }
}
