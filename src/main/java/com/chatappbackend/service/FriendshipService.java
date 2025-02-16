package com.chatappbackend.service;

import com.chatappbackend.dto.FriendshipStatus;
import com.chatappbackend.models.Friendship;
import com.chatappbackend.models.User;
import com.chatappbackend.repository.FriendshipRepository;
import com.chatappbackend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FriendshipService {
  private final FriendshipRepository friendshipRepository;
  private final UserRepository userRepository;

  public FriendshipService(
      UserRepository userRepository, FriendshipRepository friendshipRepository) {
    this.friendshipRepository = friendshipRepository;
    this.userRepository = userRepository;
  }

  public Friendship sendFriendRequest(String senderUsername, String receiverUsername) {
    User sender = userRepository.findByUsername(senderUsername).orElseThrow();
    User receiver = userRepository.findByUsername(receiverUsername).orElseThrow();

    User user1 = sender.getId() < receiver.getId() ? sender : receiver;
    User user2 = sender.getId() < receiver.getId() ? receiver : sender;

    if (friendshipRepository.findByUser1AndUser2(user1, user2).isPresent()) {
      throw new IllegalStateException("Friend request already exists");
    }

    Friendship friendship =
        Friendship.builder()
            .user1(user1)
            .user2(user2)
            .friendshipStatus(FriendshipStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    return friendshipRepository.save(friendship);
  }

  public Friendship acceptFriendRequest(Long friendshipId) {
    Friendship friendship = friendshipRepository.findById(friendshipId).orElseThrow();
    friendship.setFriendshipStatus(FriendshipStatus.ACCEPTED);
    return friendshipRepository.save(friendship);
  }

  public void removeFriend(Long friendshipId) {
    friendshipRepository.deleteById(friendshipId);
  }

  public List<Friendship> getFriends(String username, String status) {
    User user = userRepository.findByUsername(username).orElseThrow();
    List<Friendship> friendsList =
        friendshipRepository.findByUser1AndFriendshipStatus(user, FriendshipStatus.valueOf(status));
    friendsList.addAll(
        friendshipRepository.findByUser2AndFriendshipStatus(
            user, FriendshipStatus.valueOf(status)));
    return friendsList;
  }
}
