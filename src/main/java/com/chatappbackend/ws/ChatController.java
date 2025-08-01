package com.chatappbackend.ws;

import com.chatappbackend.dto.message.MessageDTO;
import com.chatappbackend.dto.wsmessage.IncomingWSChatMessage;
import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.Message;
import com.chatappbackend.models.User;
import com.chatappbackend.service.ChatService;
import com.chatappbackend.service.UserService;
import java.security.Principal;
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
  private final SimpMessagingTemplate messagingTemplate;

  public ChatController(
      ChatService chatService, UserService userService, SimpMessagingTemplate messagingTemplate) {
    this.chatService = chatService;
    this.userService = userService;
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

    messagingTemplate.convertAndSend(
        "/topic/chatroom/" + roomId + "/",
        new MessageDTO(
            chatService.saveMessage(
                Message.builder()
                    .content(message.getContent())
                    .sender(user)
                    .chatRoom(chatRoom)
                    .mediaUrl(message.getMediaUrl())
                    .build(),
                chatRoom)));
  }
}
