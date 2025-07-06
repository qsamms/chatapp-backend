package com.chatappbackend.dto.chatroom;

import com.chatappbackend.models.ChatRoom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class ChatRoomDTO {
  private UUID id;

  private String name;

  private String createdBy;

  private LocalDateTime createdAt;

  private List<String> participants;

  public ChatRoomDTO(ChatRoom chatRoom) {
    this.id = chatRoom.getId();
    this.name = chatRoom.getName();
    this.createdAt = chatRoom.getCreatedAt();
    this.createdBy = chatRoom.getCreatedBy().getUsername();
    this.participants =
        chatRoom.getParticipants().stream()
            .map(participant -> participant.getUser().getUsername())
            .collect(Collectors.toList());
  }
}
