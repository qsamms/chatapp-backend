package com.chatappbackend.controller;

import com.chatappbackend.dto.user.UserDTO;
import com.chatappbackend.models.User;
import com.chatappbackend.service.UserService;
import java.security.Principal;
import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/heartbeat")
public class HeartbeatController {
  private final UserService userService;

  public HeartbeatController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/")
  public ResponseEntity<?> heartbeat(Principal principal) {
    User reqUser = userService.getUser(principal.getName());

    reqUser.setLastHeartbeat(Instant.now());
    User updated = userService.saveUser(reqUser);
    return ResponseEntity.ok().body(new UserDTO(updated));
  }
}
