package com.chatappbackend.ws;

import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.User;
import com.chatappbackend.service.ChatService;
import com.chatappbackend.service.UserService;
import java.security.Principal;
import java.util.UUID;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomSubscriptionInterceptor implements ChannelInterceptor {
  private final ChatService chatService;
  private final UserService userService;

  public ChatRoomSubscriptionInterceptor(ChatService chatService, UserService userService) {
    this.chatService = chatService;
    this.userService = userService;
  }

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
      Principal user = accessor.getUser();
      String destination = accessor.getDestination();

      if (user == null) {
        throw new AccessDeniedException("User not authenticated");
      }

      if (destination != null && destination.startsWith("/topic/chatroom.")) {
        String roomIdStr = destination.substring("/topic/chatroom.".length());
        UUID roomId = UUID.fromString(roomIdStr);

        User u = userService.getUser(user.getName());
        ChatRoom room = chatService.getChatRoom(roomId);

        if (!chatService.isUserInChatRoom(u, room)) {
          throw new AccessDeniedException("User is not member of chat room");
        }
      }
    }
    return message;
  }
}
