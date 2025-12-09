package com.chatappbackend.controller;

import com.chatappbackend.dto.friendship.FriendRequestDTO;
import com.chatappbackend.dto.friendship.FriendRequestSentDTO;
import com.chatappbackend.dto.friendship.FriendshipDTO;
import com.chatappbackend.dto.friendship.FriendshipRequest;
import com.chatappbackend.models.Friendship;
import com.chatappbackend.models.User;
import com.chatappbackend.service.FriendshipService;
import com.chatappbackend.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friendships")
public class FriendshipController {
  private final FriendshipService friendshipService;
  private final UserService userService;

  public FriendshipController(FriendshipService friendshipService, UserService userService) {
    this.friendshipService = friendshipService;
    this.userService = userService;
  }

  @GetMapping("/")
  public ResponseEntity<Map<String, Object>> getFriends(Principal principal) {
    User reqUser = userService.getUser(principal.getName());

    List<FriendshipDTO> acceptedFriendships =
        friendshipService.getAcceptedFriendships(principal.getName()).stream()
            .map(friendship -> new FriendshipDTO(friendship, reqUser))
            .toList();
    List<FriendRequestDTO> pendingReceivedFriendships =
        friendshipService.getPendingReceivedFriendships(principal.getName()).stream()
            .map(friendRequest -> new FriendRequestDTO(friendRequest, reqUser))
            .toList();
    List<FriendRequestSentDTO> pendingSentFriendships =
        friendshipService.getPendingSentFriendships(principal.getName()).stream()
            .map(friendRequest -> new FriendRequestSentDTO(friendRequest))
            .toList();

    return ResponseEntity.ok()
        .body(
            Map.of(
                "accepted",
                acceptedFriendships,
                "pendingSent",
                pendingSentFriendships,
                "pendingReceived",
                pendingReceivedFriendships));
  }

  @GetMapping("/accepted/")
  public ResponseEntity<List<FriendshipDTO>> getAcceptedFriends(Principal principal) {
    User reqUser = userService.getUser(principal.getName());
    List<FriendshipDTO> acceptedFriendships =
        friendshipService.getAcceptedFriendships(principal.getName()).stream()
            .map(friendship -> new FriendshipDTO(friendship, reqUser))
            .toList();
    return ResponseEntity.ok().body(acceptedFriendships);
  }

  @GetMapping("/pending-sent/")
  public ResponseEntity<List<FriendRequestSentDTO>> getPendingSentFriends(Principal principal) {
    User reqUser = userService.getUser(principal.getName());
    List<FriendRequestSentDTO> pendingSentFriendships =
        friendshipService.getPendingSentFriendships(principal.getName()).stream()
            .map(friendRequest -> new FriendRequestSentDTO(friendRequest))
            .toList();
    return ResponseEntity.ok().body(pendingSentFriendships);
  }

  @GetMapping("/pending-received/")
  public ResponseEntity<List<FriendRequestDTO>> getPendingReceivedFriends(Principal principal) {
    User reqUser = userService.getUser(principal.getName());
    List<FriendRequestDTO> pendingReceivedFriendships =
        friendshipService.getPendingReceivedFriendships(principal.getName()).stream()
            .map(friendRequest -> new FriendRequestDTO(friendRequest, reqUser))
            .toList();
    return ResponseEntity.ok().body(pendingReceivedFriendships);
  }

  @PostMapping("/send/")
  public ResponseEntity<?> sendFriendRequest(
      @Valid @RequestBody FriendshipRequest friendshipRequest, Principal principal) {
    User reqUser = userService.getUser(principal.getName());
    try {
      Friendship friendship =
          friendshipService.sendFriendRequest(principal.getName(), friendshipRequest.getUsername());
      URI location = URI.create("/friendships/" + friendship.getId() + "/");
      return ResponseEntity.created(location).body(new FriendRequestSentDTO(friendship));
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(Map.of("message", "Friend request already exists"));
    }
  }

  @PostMapping("/accept/{friendshipId}/")
  public ResponseEntity<FriendshipDTO> accept(
      @PathVariable Long friendshipId, Principal principal) {
    User reqUser = userService.getUser(principal.getName());
    return ResponseEntity.ok()
        .body(new FriendshipDTO(friendshipService.acceptFriendRequest(friendshipId), reqUser));
  }

  @DeleteMapping("/{friendshipId}/")
  public ResponseEntity<Void> removeFriend(@PathVariable Long friendshipId) {
    friendshipService.removeFriend(friendshipId);
    return ResponseEntity.noContent().build();
  }
}
