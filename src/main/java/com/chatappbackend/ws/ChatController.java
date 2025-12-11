package com.chatappbackend.ws;

import com.chatappbackend.document.MessageDocument;
import com.chatappbackend.dto.message.MessageDTO;
import com.chatappbackend.dto.wsmessage.IncomingWSChatMessage;
import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.Message;
import com.chatappbackend.models.User;
import com.chatappbackend.service.ChatService;
import com.chatappbackend.service.ElasticService;
import com.chatappbackend.service.UserService;
import java.security.Principal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
  private final ChatService chatService;
  private final UserService userService;
  private final ElasticService elasticService;
  private final SimpMessagingTemplate messagingTemplate;

  public ChatController(
      ChatService chatService,
      UserService userService,
      ElasticService elasticService,
      SimpMessagingTemplate messagingTemplate) {
    this.chatService = chatService;
    this.userService = userService;
    this.elasticService = elasticService;
    this.messagingTemplate = messagingTemplate;
  }

  @MessageMapping("/chatroom/{roomId}/send/")
  public void sendMessage(
      @DestinationVariable UUID roomId, IncomingWSChatMessage message, Principal principal)
      throws AccessDeniedException {
    if (principal == null) {
      throw new AccessDeniedException("Unauthorized websocket message");
    }

    User user = userService.getUser(principal.getName());
    ChatRoom chatRoom = chatService.getChatRoom(roomId);
    MessageDTO msg =
        new MessageDTO(
            chatService.saveMessage(
                Message.builder()
                    .content(message.getContent())
                    .sender(user)
                    .chatRoom(chatRoom)
                    .mediaUrl(message.getMediaUrl())
                    .build(),
                chatRoom));

    elasticService.indexMessage(
        MessageDocument.builder()
            .user(user.getId())
            .messageId(msg.getId().toString())
            .room(chatRoom.getId().toString())
            .text(message.getContent())
            .timestamp(Instant.now())
            .build());

    messagingTemplate.convertAndSend("/topic/chatroom/" + roomId + "/", msg);
  }

  @MessageMapping("/dm/{targetUser}/send/")
  public void sendDmMessage(
      @DestinationVariable String targetUser, IncomingWSChatMessage message, Principal principal)
      throws AccessDeniedException {
    if (principal == null) {
      throw new AccessDeniedException("Unauthorized websocket message");
    }

    User sendUser = userService.getUser(principal.getName());
    User target = userService.getUser(targetUser);
    ChatRoom chatRoom = chatService.getOrCreateDm(sendUser, target);

    MessageDTO msg =
        new MessageDTO(
            chatService.saveMessage(
                Message.builder()
                    .content(message.getContent())
                    .sender(sendUser)
                    .chatRoom(chatRoom)
                    .mediaUrl(message.getMediaUrl())
                    .build(),
                chatRoom));

    messagingTemplate.convertAndSend("/topic/chatroom/" + chatRoom.getId() + "/", msg);
  }
}
