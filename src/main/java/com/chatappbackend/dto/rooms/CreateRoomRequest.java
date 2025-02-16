package com.chatappbackend.dto.rooms;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomRequest {
  @NotBlank(message = "chat room name is required")
  private String name;
}
