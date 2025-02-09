package com.chatappbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignUpResponse {
  private String username;
  private String email;
}
