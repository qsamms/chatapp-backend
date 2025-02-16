package com.chatappbackend.views;

import com.chatappbackend.dto.rooms.CreateRoomRequest;
import com.chatappbackend.dto.rooms.InviteRequest;
import com.chatappbackend.dto.rooms.RoomsRequest;
import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.ChatRoomParticipant;
import com.chatappbackend.models.User;
import com.chatappbackend.service.ChatService;
import com.chatappbackend.service.UserService;
import com.chatappbackend.utils.MapUtil;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
public class ChatRoomController {
  private final ChatService chatService;
  private final UserService userService;

  public ChatRoomController(ChatService chatService, UserService userService) {
    this.chatService = chatService;
    this.userService = userService;
  }

  @PostMapping("/")
  public ResponseEntity<Map<String, Object>> createRoom(@Valid @RequestBody CreateRoomRequest req) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User reqUser = userService.getUser(authentication.getName());
    ChatRoom chatRoom = ChatRoom.builder().createdBy(reqUser).name(req.getName()).build();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(MapUtil.toMap(chatService.saveChatRoom(chatRoom)));
  }

  @GetMapping("/")
  public ResponseEntity<Map<String, Object>> getRooms(@Valid @RequestBody RoomsRequest req) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = userService.getUser(authentication.getName());
    List<ChatRoom> chatRooms = List.of();
    if (req.getType().equals("invited")) {
      chatRooms = chatService.getInvitedUserChatRooms(currentUser.getId());
    } else if (req.getType().equals("accepted")) {
      chatRooms = chatService.getAcceptedUserChatRooms(currentUser.getId());
    }
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("rooms", chatRooms));
  }

  @PostMapping("/{roomId}/invite/")
  public ResponseEntity<Map<String, Object>> invite(
      @PathVariable UUID roomId, @Valid @RequestBody InviteRequest req) {
    User targetUser = userService.getUser(req.getUsername());
    ChatRoom chatRoom = chatService.getChatRoom(roomId);
    ChatRoomParticipant newParticipant =
        ChatRoomParticipant.builder().chatRoom(chatRoom).user(targetUser).build();
    return ResponseEntity.status(HttpStatus.OK)
        .body(MapUtil.toMap(chatService.saveChatParticipant(newParticipant)));
  }

  @PostMapping("/{roomId}/accept/")
  public ResponseEntity<Void> accept(@PathVariable UUID roomId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = userService.getUser(authentication.getName());
    chatService.acceptChatRoomInvitation(roomId, currentUser);
    return ResponseEntity.ok().build();
  }
}
