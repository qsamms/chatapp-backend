package com.chatappbackend.dto.friendship;

import com.chatappbackend.dto.user.UserDTO;
import com.chatappbackend.models.Friendship;
import com.chatappbackend.models.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class FriendshipDTO {
  private Long id;

  private UserDTO friend;

  private FriendshipStatus status;

  private Instant createdAt;

  private Instant updatedAt;

  public FriendshipDTO(Friendship friendship, User reqUser) {
    this.id = friendship.getId();
    this.friend = new UserDTO(reqUser.getUsername().equals(friendship.getUser1().getUsername()) ? friendship.getUser2() : friendship.getUser1());
    this.status = friendship.getFriendshipStatus();
    this.createdAt = friendship.getCreatedAt();
    this.updatedAt = friendship.getUpdatedAt();
  }
}
