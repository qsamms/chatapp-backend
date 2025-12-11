package com.chatappbackend.controller;

import com.chatappbackend.document.MessageDocument;
import com.chatappbackend.dto.chatroom.ChatRoomSmallDTO;
import com.chatappbackend.dto.search.SearchRequestDTO;
import com.chatappbackend.dto.search.SearchResponseDTO;
import com.chatappbackend.dto.user.UserDTO;
import com.chatappbackend.models.ChatRoom;
import com.chatappbackend.models.User;
import com.chatappbackend.service.ChatService;
import com.chatappbackend.service.ElasticService;
import com.chatappbackend.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
public class SearchController {
  private final ElasticService elasticService;
  private final ChatService chatService;
  private final UserService userService;

  public SearchController(
      ElasticService elasticService, ChatService chatService, UserService userService) {
    this.elasticService = elasticService;
    this.chatService = chatService;
    this.userService = userService;
  }

  @PostMapping("/")
  public ResponseEntity<?> searchAll(
      @Valid @RequestBody SearchRequestDTO searchRequest, Principal principal) {
    String username = principal.getName();
    User user = userService.getUser(username);
    List<ChatRoom> chatRooms = chatService.getAcceptedUserChatRooms(user.getId());
    List<MessageDocument> messages =
        elasticService.searchMessages(
            searchRequest.getText(), chatRooms.stream().map(ChatRoomSmallDTO::new).toList());

    List<Long> userIds = messages.stream().map(m -> m.getUser()).toList();
    List<User> users = userService.getUsersByIds(userIds);
    Map<Long, UserDTO> userMap =
        users.stream().collect(Collectors.toMap(User::getId, UserDTO::new));

    Map<UUID, ChatRoomSmallDTO> chatRoomMap =
        chatRooms.stream().collect(Collectors.toMap(ChatRoom::getId, ChatRoomSmallDTO::new));

    List<SearchResponseDTO> searchResponse =
        messages.stream()
            .map(
                m ->
                    new SearchResponseDTO(
                        userMap.get(m.getUser()),
                        chatRoomMap.get(UUID.fromString(m.getRoom())),
                        m.getText(),
                        m.getTimestamp(),
                        m.getMessageId()))
            .toList();

    return ResponseEntity.ok().body(Map.of("hits", searchResponse));
  }
}
