package com.chatappbackend.repository;

import com.chatappbackend.dto.friendship.FriendshipStatus;
import com.chatappbackend.models.Friendship;
import com.chatappbackend.models.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
  List<Friendship> findByUser1AndFriendshipStatus(User user1, FriendshipStatus friendshipStatus);

  List<Friendship> findByUser2AndFriendshipStatus(User user2, FriendshipStatus friendshipStatus);

  Optional<Friendship> findByUser1AndUser2(User user1, User user2);
}
