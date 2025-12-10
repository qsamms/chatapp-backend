package com.chatappbackend.dto.search;

import com.chatappbackend.dto.user.UserDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SearchResponseDTO {
  private UserDTO user;
  private String roomId;
  private String text;
  private String timestamp;

  public SearchResponseDTO(UserDTO user, String roomId, String text, String timestamp) {
    this.user = user;
    this.roomId = roomId;
    this.text = text;
    this.timestamp = timestamp;
  }
}
