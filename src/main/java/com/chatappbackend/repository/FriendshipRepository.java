package com.chatappbackend.repository;

import com.chatappbackend.dto.friendship.FriendshipStatus;
import com.chatappbackend.models.Friendship;
import com.chatappbackend.models.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
  List<Friendship> findByUser1AndFriendshipStatus(User user1, FriendshipStatus friendshipStatus);

  List<Friendship> findByUser2AndFriendshipStatus(User user2, FriendshipStatus friendshipStatus);

  List<Friendship> findBySenderAndFriendshipStatus(User sender, FriendshipStatus friendshipStatus);

  @Query(
      """
  SELECT f FROM Friendship f
  WHERE (f.user1 = :user OR f.user2 = :user)
  AND f.sender <> :user
  AND f.friendshipStatus = :status
  """)
  List<Friendship> findFriendshipsWhereUserIsNotSender(
      @Param("user") User user, @Param("status") FriendshipStatus status);

  Optional<Friendship> findByUser1AndUser2(User user1, User user2);
}
