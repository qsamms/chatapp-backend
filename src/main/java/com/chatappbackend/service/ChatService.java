package com.chatappbackend.service;

import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.ChatRoomParticipant;
import com.chatappbackend.models.Message;
import com.chatappbackend.models.User;
import com.chatappbackend.repository.ChatRoomParticipantRepository;
import com.chatappbackend.repository.ChatRoomRepository;
import com.chatappbackend.repository.MessageRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  public Optional<ChatRoomParticipant> getChatRoomParticipant(ChatRoom chatRoom, User user) {
    return chatRoomParticipantRepository.findByChatRoomAndUser(chatRoom, user);
  }

  public List<ChatRoomParticipant> getChatRoomParticipants(ChatRoom chatRoom) {
    return chatRoomParticipantRepository.findByChatRoom(chatRoom);
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

  public List<ChatRoom> getChatRoomsCreatedByUser(User user) {
    return chatRoomRepository.findByCreatedBy(user);
  }

  public List<Message> getMessagesInChatRoom(UUID chatRoomId) {
    return messageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId);
  }

  public List<Message> getUserMessagesInChatRoom(UUID chatRoomId, Long userId) {
    return messageRepository.findMessagesByChatRoomAndUser(chatRoomId, userId);
  }

  public void acceptChatRoomInvitation(UUID chatRoomId, User user) {
    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
    ChatRoomParticipant chatRoomParticipant =
        chatRoomParticipantRepository.findByChatRoomAndUser(chatRoom, user).orElseThrow();
    chatRoomParticipant.setHasAccepted(true);
    chatRoomParticipantRepository.save(chatRoomParticipant);
  }
}
