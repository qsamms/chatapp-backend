package com.chatappbackend.repository;

import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.Message;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {
  Page<Message> findByChatRoomIdOrderByTimestampDesc(UUID chatRoomId, Pageable pageable);

  Page<Message> findByChatRoomIdAndTimestampBefore(
      UUID chatRoomId, Instant before, Pageable pageable);

  Page<Message> findByChatRoomIdAndTimestampAfter(
      UUID chatRoomId, Instant after, Pageable pageable);

  @Query("SELECT MAX(m.sequenceNumber) FROM Message m WHERE m.chatRoom = :chatRoom")
  Long findMaxSequenceNumberByChatRoom(@Param("chatRoom") ChatRoom chatRoom);
}
