package com.chatappbackend.dto.chatroominvite;

import com.chatappbackend.dto.chatroom.ChatRoomDTO;
import com.chatappbackend.models.ChatRoomInviteLink;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ChatRoomInviteLinkDTO {
  private UUID id;

  private ChatRoomDTO chatRoom;

  private Instant expiration;

  public ChatRoomInviteLinkDTO(ChatRoomInviteLink inviteLink) {
    this.id = inviteLink.getId();
    this.chatRoom = new ChatRoomDTO(inviteLink.getChatRoom());
    this.expiration = inviteLink.getExpiration();
  }
}
