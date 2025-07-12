package com.chatappbackend.service;

import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.ChatRoomParticipant;
import com.chatappbackend.models.Message;
import com.chatappbackend.models.User;
import com.chatappbackend.repository.ChatRoomParticipantRepository;
import com.chatappbackend.repository.ChatRoomRepository;
import com.chatappbackend.repository.MessageRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
  private final ChatRoomParticipantRepository chatRoomParticipantRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final MessageRepository messageRepository;

  public ChatService(
      ChatRoomRepository chatRoomRepository,
      ChatRoomParticipantRepository chatRoomParticipantRepository,
      MessageRepository messageRepository) {
    this.chatRoomRepository = chatRoomRepository;
    this.messageRepository = messageRepository;
    this.chatRoomParticipantRepository = chatRoomParticipantRepository;
  }

  public ChatRoomParticipant saveChatParticipant(ChatRoomParticipant chatRoomParticipant) {
    return chatRoomParticipantRepository.save(chatRoomParticipant);
  }

  public List<ChatRoom> getAcceptedUserChatRooms(Long userId) {
    return chatRoomRepository.findAcceptedChatRoomsByUserId(userId);
  }

  public List<ChatRoom> getInvitedUserChatRooms(Long userId) {
    return chatRoomRepository.findInvitedCharRoomsByUserId(userId);
  }

  public ChatRoom getChatRoom(UUID chatRoomId) {
    return chatRoomRepository.findById(chatRoomId).orElseThrow();
  }

  public ChatRoom saveChatRoom(ChatRoom chatRoom) {
    return chatRoomRepository.save(chatRoom);
  }

  public Page<Message> getMessagesInChatRoom(UUID chatRoomId, int limit) {
    Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
    return messageRepository.findByChatRoomIdOrderByTimestampDesc(chatRoomId, pageable);
  }

  public Page<Message> getMessagesInChatRoomBefore(
      UUID chatRoomId, LocalDateTime before, int limit) {
    Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
    return messageRepository.findByChatRoomIdAndTimestampBefore(chatRoomId, before, pageable);
  }

  public Page<Message> getMessagesInChatRoomAfter(UUID chatRoomId, LocalDateTime after, int limit) {
    Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "timestamp"));
    return messageRepository.findByChatRoomIdAndTimestampAfter(chatRoomId, after, pageable);
  }

  public void acceptChatRoomInvitation(UUID chatRoomId, User user) {
    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
    ChatRoomParticipant chatRoomParticipant =
        chatRoomParticipantRepository.findByChatRoomAndUser(chatRoom, user).orElseThrow();
    chatRoomParticipant.setHasAccepted(true);
    chatRoomParticipant.setJoinedAt(LocalDateTime.now());
    chatRoomParticipantRepository.save(chatRoomParticipant);
  }

  @Transactional
  public Message saveMessage(Message message, ChatRoom chatRoom) {
    // Locking the row to avoid race condition of concurrent threads reading the next sequence
    // number,
    // and attempting to write the same next sequence number to db.
    ChatRoom lockedChatRoom = this.chatRoomRepository.lockChatRoomById(chatRoom.getId());

    Long maxSequenceNumber = this.messageRepository.findMaxSequenceNumberByChatRoom(chatRoom);
    message.setSequenceNumber((maxSequenceNumber == null) ? 0 : maxSequenceNumber + 1);
    return messageRepository.save(message);
  }

  public boolean isUserInChatRoom(User user, ChatRoom chatRoom) {
    return chatRoomParticipantRepository.existsByChatRoomAndUser(chatRoom, user);
  }
}
