package com.chatappbackend.dto.rooms;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteRequest {
  @NotBlank(message = "username for invited person is required")
  private String username;
}
