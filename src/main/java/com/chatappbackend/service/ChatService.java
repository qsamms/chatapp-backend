package com.chatappbackend.service;

import com.chatappbackend.models.*;
import com.chatappbackend.repository.ChatRoomInviteLinkRepository;
import com.chatappbackend.repository.ChatRoomParticipantRepository;
import com.chatappbackend.repository.ChatRoomRepository;
import com.chatappbackend.repository.MessageRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.Duration;
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
  private final ChatRoomInviteLinkRepository chatRoomInviteLinkRepository;

  public ChatService(
      ChatRoomRepository chatRoomRepository,
      ChatRoomParticipantRepository chatRoomParticipantRepository,
      MessageRepository messageRepository,
      ChatRoomInviteLinkRepository chatRoomInviteLinkRepository) {
    this.chatRoomRepository = chatRoomRepository;
    this.messageRepository = messageRepository;
    this.chatRoomParticipantRepository = chatRoomParticipantRepository;
    this.chatRoomInviteLinkRepository = chatRoomInviteLinkRepository;
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

  public ChatRoom getOrCreateDm(User user1, User user2) {
    ChatRoom chatRoom = chatRoomRepository.findDmRoomByParticipants(user1.getId(), user2.getId());
    if (chatRoom == null) {
      chatRoom = ChatRoom.builder().name("").isDm(true).createdBy(user1).build();
      chatRoom = chatRoomRepository.save(chatRoom);
      List<ChatRoomParticipant> participants = List.of(
              ChatRoomParticipant.builder().user(user1).hasAccepted(true).joinedAt(Instant.now()).chatRoom(chatRoom).build(),
              ChatRoomParticipant.builder().user(user2).hasAccepted(true).joinedAt(Instant.now()).chatRoom(chatRoom).build()
      );
      chatRoom.setParticipants(participants);
      chatRoomRepository.save(chatRoom);
    }
    return chatRoom;
  }

  public ChatRoom saveChatRoom(ChatRoom chatRoom) {
    return chatRoomRepository.save(chatRoom);
  }

  public Page<Message> getMessagesInChatRoom(UUID chatRoomId, int limit) {
    Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
    return messageRepository.findByChatRoomIdOrderByTimestampDesc(chatRoomId, pageable);
  }

  public Page<Message> getMessagesInChatRoomBefore(UUID chatRoomId, Instant before, int limit) {
    Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
    return messageRepository.findByChatRoomIdAndTimestampBefore(chatRoomId, before, pageable);
  }

  public Page<Message> getMessagesInChatRoomAfter(UUID chatRoomId, Instant after, int limit) {
    Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "timestamp"));
    return messageRepository.findByChatRoomIdAndTimestampAfter(chatRoomId, after, pageable);
  }

  public List<ChatRoomParticipant> getUsersInChatRoom(UUID chatRoomId, boolean hasAccepted) {
    return chatRoomParticipantRepository.findByChatRoomIdAndHasAccepted(chatRoomId, hasAccepted);
  }

  public void acceptChatRoomInvitation(UUID chatRoomId, User user) {
    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
    ChatRoomParticipant chatRoomParticipant =
        chatRoomParticipantRepository.findByChatRoomAndUser(chatRoom, user).orElseThrow();
    chatRoomParticipant.setHasAccepted(true);
    chatRoomParticipant.setJoinedAt(Instant.now());
    chatRoomParticipantRepository.save(chatRoomParticipant);
  }

  @Transactional
  public Message saveMessage(Message message, ChatRoom chatRoom) {
    // Locking the row to avoid race condition of concurrent threads reading the sequence
    // number and attempting to write the same next sequence number to db.
    ChatRoom lockedChatRoom = this.chatRoomRepository.lockChatRoomById(chatRoom.getId());

    Long maxSequenceNumber = this.messageRepository.findMaxSequenceNumberByChatRoom(chatRoom);
    message.setSequenceNumber((maxSequenceNumber == null) ? 0 : maxSequenceNumber + 1);
    return messageRepository.save(message);
  }

  public boolean isUserInChatRoom(User user, ChatRoom chatRoom) {
    return chatRoomParticipantRepository.existsByChatRoomAndUser(chatRoom, user);
  }

  public ChatRoomInviteLink getChatRoomInviteLink(UUID id) {
    return chatRoomInviteLinkRepository.getReferenceById(id);
  }

  public ChatRoomInviteLink generateChatRoomInviteLink(ChatRoom chatRoom) {
    Instant plusFiveMinutes = Instant.now().plus(Duration.ofMinutes(5));
    return chatRoomInviteLinkRepository.save(
        ChatRoomInviteLink.builder().expiration(plusFiveMinutes).chatRoom(chatRoom).build());
  }
}
