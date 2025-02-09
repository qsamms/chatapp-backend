package com.chatappbackend.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class AuthRequest {
  @NotBlank(message = "Username is required")
  private String username;
  @NotBlank(message = "Password is required")
  private String password;
}
