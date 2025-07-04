package com.chatappbackend.repository;

import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.ChatRoomParticipant;
import com.chatappbackend.models.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, UUID> {

  Optional<ChatRoomParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

  List<ChatRoomParticipant> findByChatRoom(ChatRoom chatRoom);

  boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);
}
