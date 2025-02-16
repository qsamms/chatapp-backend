package com.chatappbackend.dto.friendship;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendshipRequest {
  @NotBlank private String username;
}
