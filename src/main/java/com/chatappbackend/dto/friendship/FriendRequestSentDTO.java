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

    private UserDTO reciever;

    private FriendshipStatus status;

    private Instant createdAt;

    private Instant updatedAt;

    public FriendRequestSentDTO(Friendship friendship, User reqUser) {
        this.id = friendship.getId();
        this.reciever = new UserDTO(!friendship.getUser1().getUsername().equals(reqUser.getUsername()) ? friendship.getUser1() : friendship.getUser2());
        this.status = friendship.getFriendshipStatus();
        this.createdAt = friendship.getCreatedAt();
        this.updatedAt = friendship.getUpdatedAt();
    }
}
