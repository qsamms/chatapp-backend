package com.chatappbackend.controller;

import com.chatappbackend.document.MessageDocument;
import com.chatappbackend.dto.chatroom.ChatRoomDTO;
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
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  @GetMapping("/")
  public ResponseEntity<?> searchAll(
      @Valid @RequestBody SearchRequestDTO searchRequest, Principal principal) {
    String username = principal.getName();
    User user = userService.getUser(username);
    List<ChatRoom> chatRooms = chatService.getAcceptedUserChatRooms(user.getId());
    List<MessageDocument> messages =
        elasticService.searchMessages(
            searchRequest.getText(), chatRooms.stream().map(ChatRoomDTO::new).toList());

    List<Long> userIds = messages.stream().map(m -> m.getUser()).toList();
    List<User> users = userService.getUsersByIds(userIds);
    Map<Long, UserDTO> userMap =
        users.stream().collect(Collectors.toMap(User::getId, UserDTO::new));

    List<SearchResponseDTO> searchResponse =
        messages.stream()
            .map(
                m ->
                    new SearchResponseDTO(
                        userMap.get(m.getUser()), m.getRoom(), m.getText(), m.getTimestamp()))
            .toList();

    return ResponseEntity.ok().body(Map.of("hits", searchResponse));
  }
}
