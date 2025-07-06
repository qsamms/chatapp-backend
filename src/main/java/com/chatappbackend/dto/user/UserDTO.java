package com.chatappbackend.dto.user;

import com.chatappbackend.models.User;

public class UserDTO {
  private Long id;

  private String username;

  private String email;

  private Role role;

  public UserDTO(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.role = user.getRole();
  }
}
