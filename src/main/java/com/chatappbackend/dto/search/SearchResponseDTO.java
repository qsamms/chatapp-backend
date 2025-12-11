package com.chatappbackend.dto.search;

import com.chatappbackend.dto.chatroom.ChatRoomSmallDTO;
import com.chatappbackend.dto.user.UserDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SearchResponseDTO {
  private UserDTO user;
  private ChatRoomSmallDTO room;
  private String text;
  private String timestamp;
  private String messageId;

  public SearchResponseDTO(
      UserDTO user, ChatRoomSmallDTO chatRoom, String text, String timestamp, String messageId) {
    this.user = user;
    this.room = chatRoom;
    this.text = text;
    this.timestamp = timestamp;
    this.messageId = messageId;
  }
}
