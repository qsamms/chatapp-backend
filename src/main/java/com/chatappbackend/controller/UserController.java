package com.chatappbackend.controller;

import com.chatappbackend.dto.user.UpdateUserReqDTO;
import com.chatappbackend.dto.user.UserDTO;
import com.chatappbackend.models.User;
import com.chatappbackend.service.UserService;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me/")
  public ResponseEntity<UserDTO> me(Principal principal) {
    return ResponseEntity.ok().body(new UserDTO(userService.getUser(principal.getName())));
  }

  @PostMapping("/me/")
  public ResponseEntity<UserDTO> postMe(@RequestBody UpdateUserReqDTO req, Principal principal) {
    User reqUser = userService.getUser(principal.getName());
    reqUser.setBio(req.getBio());
    reqUser.setDisplayName(req.getDisplayName());
    return ResponseEntity.ok().body(new UserDTO(userService.saveUser(reqUser)));
  }
}
