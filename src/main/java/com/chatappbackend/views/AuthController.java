package com.chatappbackend.views;

import com.chatappbackend.dto.AuthRequest;
import com.chatappbackend.dto.SignUpRequest;
import com.chatappbackend.dto.SignUpResponse;
import com.chatappbackend.models.User;
import com.chatappbackend.service.UserService;
import com.chatappbackend.utils.JwtUtil;
import com.chatappbackend.utils.MapUtil;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserService userService;

  public AuthController(
      AuthenticationManager authManager, JwtUtil jwtUtil, UserService userService) {
    this.authenticationManager = authManager;
    this.jwtUtil = jwtUtil;
    this.userService = userService;
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    UserDetails userDetails = userService.getUser(request.getUsername());
    String token = jwtUtil.generateToken(userDetails.getUsername());
    return ResponseEntity.ok(Map.of("token", token));
  }

  @PostMapping("/signup")
  public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody SignUpRequest request) {
    try {
      User newUser =
          userService.createUser(request.getUsername(), request.getPassword(), request.getEmail());
      SignUpResponse response =
          SignUpResponse.builder()
              .username(newUser.getUsername())
              .email(newUser.getEmail())
              .build();
      return ResponseEntity.ok(MapUtil.toMap(response));
    } catch (DataIntegrityViolationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "Error creating user"));
    }
  }
}
