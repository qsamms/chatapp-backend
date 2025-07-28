package com.chatappbackend.dto.user;

import com.chatappbackend.models.User;
import lombok.Getter;

@Getter
public class UserDTO {
  private Long id;

  private String username;

  private String email;

  private String role;

  private String bio;

  private String displayName;

  private String firstName;

  private String lastName;

  public UserDTO(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.role = (user.getRole() != null) ? user.getRole().name() : null;
    this.bio = user.getBio();
    this.displayName = user.getDisplayName();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
  }
}
