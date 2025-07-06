package com.chatappbackend.views;

import com.chatappbackend.dto.friendship.FriendshipRequest;
import com.chatappbackend.dto.friendship.ListFriendsRequest;
import com.chatappbackend.service.FriendshipService;
import com.chatappbackend.utils.MapUtil;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friendships")
public class FriendshipController {
  private final FriendshipService friendshipService;

  public FriendshipController(FriendshipService friendshipService) {
    this.friendshipService = friendshipService;
  }

  @PostMapping("/send/")
  public ResponseEntity<Map<String, Object>> sendFriendRequest(
      @Valid @RequestBody FriendshipRequest friendshipRequest, Principal principal) {
    return ResponseEntity.ok(
        MapUtil.toMap(
            friendshipService.sendFriendRequest(
                principal.getName(), friendshipRequest.getUsername())));
  }

  @PostMapping("/accept/{friendshipId}/")
  public ResponseEntity<Map<String, Object>> accept(@PathVariable Long friendshipId) {
    return ResponseEntity.ok()
        .body(MapUtil.toMap(friendshipService.acceptFriendRequest(friendshipId)));
  }

  @DeleteMapping("/{friendshipId}/")
  public ResponseEntity<Map<String, Object>> removeFriend(@PathVariable Long friendshipId) {
    friendshipService.removeFriend(friendshipId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/")
  public ResponseEntity<Map<String, Object>> getFriends(
      @Valid @RequestBody ListFriendsRequest listFriendsRequest, Principal principal) {
    return ResponseEntity.ok()
        .body(
            Map.of(
                "messages",
                friendshipService.getFriends(principal.getName(), listFriendsRequest.getStatus())));
  }
}
