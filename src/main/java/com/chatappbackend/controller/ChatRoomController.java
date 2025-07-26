package com.chatappbackend.controller;

import com.chatappbackend.dto.chatroom.ChatRoomDTO;
import com.chatappbackend.dto.chatroominvite.ChatRoomInviteLinkDTO;
import com.chatappbackend.dto.message.MessageDTO;
import com.chatappbackend.dto.message.MessageReq;
import com.chatappbackend.dto.rooms.CreateRoomRequest;
import com.chatappbackend.dto.rooms.InviteRequest;
import com.chatappbackend.models.*;
import com.chatappbackend.service.ChatService;
import com.chatappbackend.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
  public ResponseEntity<ChatRoomDTO> createRoom(
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
    return ResponseEntity.created(location).body(new ChatRoomDTO(chatRoom));
  }

  @GetMapping("/")
  public ResponseEntity<Map<String, Object>> getAllRooms(Principal principal) {
    User reqUser = userService.getUser(principal.getName());

    List<ChatRoomDTO> acceptedChatRooms =
        chatService.getAcceptedUserChatRooms(reqUser.getId()).stream()
            .map(ChatRoomDTO::new)
            .toList();

    List<ChatRoomDTO> invitedChatRooms =
        chatService.getInvitedUserChatRooms(reqUser.getId()).stream()
            .map(ChatRoomDTO::new)
            .toList();

    return ResponseEntity.ok()
        .body(Map.of("accepted", acceptedChatRooms, "invited", invitedChatRooms));
  }

  @GetMapping("/accepted/")
  public ResponseEntity<List<ChatRoomDTO>> getAcceptedRooms(Principal principal) {
    User reqUser = userService.getUser(principal.getName());

    List<ChatRoomDTO> chatRooms =
        chatService.getAcceptedUserChatRooms(reqUser.getId()).stream()
            .map(ChatRoomDTO::new)
            .toList();

    return ResponseEntity.ok().body(chatRooms);
  }

  @GetMapping("/invited/")
  public ResponseEntity<List<ChatRoomDTO>> getInvitedRooms(Principal principal) {
    User reqUser = userService.getUser(principal.getName());

    List<ChatRoomDTO> chatRooms =
        chatService.getInvitedUserChatRooms(reqUser.getId()).stream()
            .map(ChatRoomDTO::new)
            .toList();

    return ResponseEntity.ok().body(chatRooms);
  }

  @PostMapping("/{roomId}/messages/")
  public ResponseEntity<Map<String, Object>> getMessages(
      @PathVariable UUID roomId,
      @RequestBody(required = false) MessageReq req,
      @RequestParam(defaultValue = "50") int limit,
      Principal principal) {
    User reqUser = userService.getUser(principal.getName());
    ChatRoom chatRoom = chatService.getChatRoom(roomId);

    if (!chatService.isUserInChatRoom(reqUser, chatRoom)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "User is not in the requested chat room"));
    }

    LocalDateTime before = req != null ? req.getBefore() : null;
    LocalDateTime after = req != null ? req.getAfter() : null;

    if (before != null && after != null)
      return ResponseEntity.badRequest()
          .body(Map.of("message", "Before and after cannot be given together"));

    Page<Message> page;
    if (before != null) {
      page = chatService.getMessagesInChatRoomBefore(chatRoom.getId(), before, limit);
    } else if (after != null) {
      page = chatService.getMessagesInChatRoomAfter(chatRoom.getId(), after, limit);
    } else {
      page = chatService.getMessagesInChatRoom(chatRoom.getId(), limit);
    }

    Map<String, Object> response = new HashMap<>();
    List<MessageDTO> messages = page.getContent().stream().map(MessageDTO::new).toList();
    response.put("messages", messages);
    response.put("hasMore", page.hasNext());
    response.put("nextCursor", messages.isEmpty() ? null : messages.getLast().getTimestamp());

    return ResponseEntity.ok(response);
  }

  @PostMapping("/{roomId}/invite/")
  public ResponseEntity<Void> invite(
      @PathVariable UUID roomId, @Valid @RequestBody InviteRequest req) {
    User targetUser = userService.getUser(req.getUsername());
    ChatRoom chatRoom = chatService.getChatRoom(roomId);
    chatService.saveChatParticipant(
        ChatRoomParticipant.builder().chatRoom(chatRoom).user(targetUser).build());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{roomId}/accept/")
  public ResponseEntity<Void> accept(@PathVariable UUID roomId, Principal principal) {
    User reqUser = userService.getUser(principal.getName());
    chatService.acceptChatRoomInvitation(roomId, reqUser);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{roomId}/invite-link/")
  public ResponseEntity<ChatRoomInviteLinkDTO> getInviteLink(@PathVariable UUID roomId, Principal principal) {
    return ResponseEntity.ok().body(new ChatRoomInviteLinkDTO(chatService.generateChatRoomInviteLink(chatService.getChatRoom(roomId))));
  }

  @PostMapping("/join/{inviteId}/")
  public ResponseEntity<?> joinRoom(@PathVariable UUID inviteId, Principal principal) {
    User reqUser = userService.getUser(principal.getName());

    ChatRoomInviteLink invite = chatService.getChatRoomInviteLink(inviteId);
    if (invite == null || LocalDateTime.now().isAfter(invite.getExpiration()) || chatService.isUserInChatRoom(reqUser, invite.getChatRoom())) {
      return ResponseEntity.badRequest().build();
    }

    ChatRoomParticipant newParticipant = ChatRoomParticipant.builder().chatRoom(invite.getChatRoom()).user(reqUser).joinedAt(LocalDateTime.now()).hasAccepted(true).build();
    chatService.saveChatParticipant(newParticipant);
    return ResponseEntity.ok().build();
  }
}
