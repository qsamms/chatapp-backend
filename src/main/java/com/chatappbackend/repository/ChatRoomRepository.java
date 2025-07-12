package com.chatappbackend.repository;

import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.User;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
  @Query(
      "SELECT cr FROM ChatRoom cr JOIN cr.participants p WHERE p.user.id = :userId AND p.hasAccepted = true")
  List<ChatRoom> findAcceptedChatRoomsByUserId(Long userId);

  @Query(
      "SELECT cr FROM ChatRoom cr JOIN cr.participants p WHERE p.user.id = :userId AND p.hasAccepted = false")
  List<ChatRoom> findInvitedCharRoomsByUserId(Long userId);

  List<ChatRoom> findByCreatedBy(User user);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM ChatRoom c WHERE c.id = :id")
  ChatRoom lockChatRoomById(@Param("id") UUID id);
}
