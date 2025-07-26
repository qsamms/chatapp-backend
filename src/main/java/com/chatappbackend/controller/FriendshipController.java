package com.chatappbackend.controller;

import com.chatappbackend.dto.friendship.FriendshipDTO;
import com.chatappbackend.dto.friendship.FriendshipRequest;
import com.chatappbackend.models.Friendship;
import com.chatappbackend.service.FriendshipService;
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

  public FriendshipController(FriendshipService friendshipService) {
    this.friendshipService = friendshipService;
  }

  @GetMapping("/")
  public ResponseEntity<Map<String, Object>> getFriends(Principal principal) {
    List<FriendshipDTO> acceptedFriendships =
        friendshipService.getAcceptedFriendships(principal.getName()).stream()
            .map(FriendshipDTO::new)
            .toList();
    List<FriendshipDTO> pendingReceivedFriendships =
        friendshipService.getPendingReceivedFriendships(principal.getName()).stream()
            .map(FriendshipDTO::new)
            .toList();
    List<FriendshipDTO> pendingSentFriendships =
        friendshipService.getPendingSentFriendships(principal.getName()).stream()
            .map(FriendshipDTO::new)
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
    List<FriendshipDTO> acceptedFriendships =
        friendshipService.getAcceptedFriendships(principal.getName()).stream()
            .map(FriendshipDTO::new)
            .toList();
    return ResponseEntity.ok().body(acceptedFriendships);
  }

  @GetMapping("/pending-sent/")
  public ResponseEntity<List<FriendshipDTO>> getPendingSentFriends(Principal principal) {
    List<FriendshipDTO> pendingSentFriendships =
        friendshipService.getPendingSentFriendships(principal.getName()).stream()
            .map(FriendshipDTO::new)
            .toList();
    return ResponseEntity.ok().body(pendingSentFriendships);
  }

  @GetMapping("/pending-received/")
  public ResponseEntity<List<FriendshipDTO>> getPendingReceivedFriends(Principal principal) {
    List<FriendshipDTO> pendingReceivedFriendships =
        friendshipService.getPendingReceivedFriendships(principal.getName()).stream()
            .map(FriendshipDTO::new)
            .toList();
    return ResponseEntity.ok().body(pendingReceivedFriendships);
  }

  @PostMapping("/send/")
  public ResponseEntity<FriendshipDTO> sendFriendRequest(
      @Valid @RequestBody FriendshipRequest friendshipRequest, Principal principal) {
    Friendship friendship =
        friendshipService.sendFriendRequest(principal.getName(), friendshipRequest.getUsername());
    URI location = URI.create("/friendships/" + friendship.getId() + "/");
    return ResponseEntity.created(location).body(new FriendshipDTO(friendship));
  }

  @PostMapping("/accept/{friendshipId}/")
  public ResponseEntity<FriendshipDTO> accept(@PathVariable Long friendshipId) {
    return ResponseEntity.ok()
        .body(new FriendshipDTO(friendshipService.acceptFriendRequest(friendshipId)));
  }

  @DeleteMapping("/{friendshipId}/")
  public ResponseEntity<Void> removeFriend(@PathVariable Long friendshipId) {
    friendshipService.removeFriend(friendshipId);
    return ResponseEntity.noContent().build();
  }
}
