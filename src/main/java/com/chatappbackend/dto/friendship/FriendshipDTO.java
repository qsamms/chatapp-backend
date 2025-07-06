package com.chatappbackend.dto.friendship;

import com.chatappbackend.models.Friendship;
import java.time.LocalDateTime;

public class FriendshipDTO {
  private Long id;

  private String user1;

  private String user2;

  private String sender;

  private FriendshipStatus status;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public FriendshipDTO(Friendship friendship) {
    this.id = friendship.getId();
    this.sender = friendship.getSender().getUsername();
    this.user1 = friendship.getUser1().getUsername();
    this.user2 = friendship.getUser2().getUsername();
    this.status = friendship.getFriendshipStatus();
    this.createdAt = friendship.getCreatedAt();
    this.updatedAt = friendship.getUpdatedAt();
  }
}
