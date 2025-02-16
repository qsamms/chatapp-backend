package com.chatappbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListFriendsRequest {
  @NotBlank private String status;
}
