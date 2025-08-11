package com.chatappbackend.dto.friendship;

import com.chatappbackend.dto.user.UserDTO;
import com.chatappbackend.models.Friendship;
import com.chatappbackend.models.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class FriendRequestSentDTO {
    private Long id;

    private UserDTO receiver;

    private FriendshipStatus status;

    private Instant createdAt;

    private Instant updatedAt;

    public FriendRequestSentDTO(Friendship friendship) {
        this.id = friendship.getId();
        User receiverUser = friendship.getUser1().getUsername().equals(friendship.getSender().getUsername()) ? friendship.getUser2() : friendship.getUser1();
        this.receiver = new UserDTO(receiverUser);
        this.status = friendship.getFriendshipStatus();
        this.createdAt = friendship.getCreatedAt();
        this.updatedAt = friendship.getUpdatedAt();
    }
}
