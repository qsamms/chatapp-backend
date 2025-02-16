package com.chatappbackend.dto.friendship;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListFriendsRequest {
  @NotBlank(message = "status is required")
  private String status;
}
