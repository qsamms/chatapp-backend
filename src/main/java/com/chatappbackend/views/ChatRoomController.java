package com.chatappbackend.views;

import com.chatappbackend.dto.chatroom.ChatRoomDTO;
import com.chatappbackend.dto.message.MessageDTO;
import com.chatappbackend.dto.rooms.CreateRoomRequest;
import com.chatappbackend.dto.rooms.InviteRequest;
import com.chatappbackend.dto.rooms.RoomsRequest;
import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.ChatRoomParticipant;
import com.chatappbackend.models.User;
import com.chatappbackend.service.ChatService;
import com.chatappbackend.service.UserService;
import com.chatappbackend.utils.MapUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
public class ChatRoomController {

  private final ChatService chatService;
  private final UserService userService;

  @PersistenceContext private EntityManager entityManager;

  public ChatRoomController(ChatService chatService, UserService userService) {
    this.chatService = chatService;
    this.userService = userService;
  }

  @PostMapping("/")
  @Transactional
  public ResponseEntity<Map<String, Object>> createRoom(
      @Valid @RequestBody CreateRoomRequest req, Principal principal) {
    User reqUser = userService.getUser(principal.getName());

    ChatRoom chatRoom =
        chatService.saveChatRoom(ChatRoom.builder().createdBy(reqUser).name(req.getName()).build());
    chatService.saveChatParticipant(
        ChatRoomParticipant.builder()
            .user(reqUser)
            .chatRoom(chatRoom)
            .joinedAt(LocalDateTime.now())
            .hasAccepted(true)
            .build());
    entityManager.flush();
    entityManager.refresh(chatRoom);

    URI location = URI.create("/rooms/" + chatRoom.getId() + "/");
    return ResponseEntity.created(location).body(MapUtil.toMap(new ChatRoomDTO(chatRoom)));
  }

  @GetMapping("/")
  public ResponseEntity<Map<String, Object>> getRooms(
      @Valid @RequestBody RoomsRequest req, Principal principal) {
    User reqUser = userService.getUser(principal.getName());

    List<ChatRoomDTO> chatRooms = List.of();
    if (req.getType().equals("invited")) {
      chatRooms =
          chatService.getInvitedUserChatRooms(reqUser.getId()).stream()
              .map(ChatRoomDTO::new)
              .collect(Collectors.toList());
    } else if (req.getType().equals("accepted")) {
      chatRooms =
          chatService.getAcceptedUserChatRooms(reqUser.getId()).stream()
              .map(ChatRoomDTO::new)
              .collect(Collectors.toList());
      ;
    }

    return ResponseEntity.ok().body(Map.of("rooms", chatRooms));
  }

  @GetMapping("/{roomId}/messages/")
  public ResponseEntity<Map<String, Object>> getMessages(
      @PathVariable UUID roomId, Principal principal) {
    User reqUser = userService.getUser(principal.getName());

    ChatRoom chatRoom = chatService.getChatRoom(roomId);
    if (!chatService.isUserInChatRoom(reqUser, chatRoom)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "User is not in the requested chat room"));
    }

    return ResponseEntity.ok()
        .body(
            Map.of(
                "messages",
                chatService.getMessagesInChatRoom(roomId).stream()
                    .map(MessageDTO::new)
                    .collect(Collectors.toList())));
  }

  @PostMapping("/{roomId}/invite/")
  public ResponseEntity<Map<String, Object>> invite(
      @PathVariable UUID roomId, @Valid @RequestBody InviteRequest req) {
    User targetUser = userService.getUser(req.getUsername());
    ChatRoom chatRoom = chatService.getChatRoom(roomId);
    ChatRoomParticipant newParticipant =
        ChatRoomParticipant.builder().chatRoom(chatRoom).user(targetUser).build();

    return ResponseEntity.ok().build();
  }

  @PostMapping("/{roomId}/accept/")
  public ResponseEntity<Void> accept(@PathVariable UUID roomId, Principal principal) {
    User reqUser = userService.getUser(principal.getName());
    chatService.acceptChatRoomInvitation(roomId, reqUser);
    return ResponseEntity.ok().build();
  }
}
