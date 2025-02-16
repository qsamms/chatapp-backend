package com.chatappbackend.repository;

import com.chatappbackend.models.Message;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {
  List<Message> findByChatRoomIdOrderByTimestampAsc(UUID chatRoomId);

  @Query(
      "SELECT m FROM Message m WHERE m.chatRoom.id = :chatRoomId AND m.sender.id = :userId ORDER BY m.timestamp ASC")
  List<Message> findMessagesByChatRoomAndUser(UUID chatRoomId, Long userId);
}
