package com.chatappbackend.views;

import com.chatappbackend.dto.FriendshipRequest;
import com.chatappbackend.dto.ListFriendsRequest;
import com.chatappbackend.models.Friendship;
import com.chatappbackend.service.FriendshipService;
import com.chatappbackend.utils.MapUtil;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
      @Valid @RequestBody FriendshipRequest friendshipRequest) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return ResponseEntity.ok(
        MapUtil.toMap(
            friendshipService.sendFriendRequest(
                authentication.getName(), friendshipRequest.getUsername())));
  }

  @PostMapping("/accept/{friendshipId}/")
  public ResponseEntity<Map<String, Object>> accept(@PathVariable Long friendshipId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(MapUtil.toMap(friendshipService.acceptFriendRequest(friendshipId)));
  }

  @DeleteMapping("/{friendshipId}/")
  public void removeFriend(@PathVariable Long friendshipId) {
    friendshipService.removeFriend(friendshipId);
  }

  @GetMapping("/")
  public List<Friendship> getFriends(@Valid @RequestBody ListFriendsRequest listFriendsRequest) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return friendshipService.getFriends(authentication.getName(), listFriendsRequest.getStatus());
  }
}
