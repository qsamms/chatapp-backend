package com.chatappbackend.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class SignUpRequest {
  @NotBlank(message = "Username is required")
  private String username;

  @NotBlank(message = "Password is required")
  private String password;

  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is required")
  private String email;
}
