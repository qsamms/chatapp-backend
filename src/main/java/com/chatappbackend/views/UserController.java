package com.chatappbackend.views;

import com.chatappbackend.dto.user.UserDTO;
import com.chatappbackend.service.UserService;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
