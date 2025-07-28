package com.chatappbackend.controller;

import com.chatappbackend.dto.auth.AuthRequest;
import com.chatappbackend.dto.auth.SignUpRequest;
import com.chatappbackend.dto.auth.SignUpResponse;
import com.chatappbackend.models.User;
import com.chatappbackend.service.UserService;
import com.chatappbackend.utils.JwtUtil;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

  @PostMapping("/login/")
  public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthRequest request) {
    UserDetails userDetails = userService.getUser(request.getUsername());
    String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
    String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
    return ResponseEntity.ok(Map.of("accessToken", accessToken, "refreshToken", refreshToken));
  }

  @PostMapping("/refresh/")
  public ResponseEntity<?> refresh(Principal principal) {
    String accessToken = jwtUtil.generateAccessToken(principal.getName());
    return ResponseEntity.ok(Map.of("accessToken", accessToken));
  }

  @PostMapping("/signup/")
  public ResponseEntity<SignUpResponse> signup(@Valid @RequestBody SignUpRequest request) {
    User newUser =
        userService.createUser(request.getEmail(), request.getPassword(), request.getFirstName(), request.getLastName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            SignUpResponse.builder()
                .username(newUser.getUsername())
                .email(newUser.getEmail())
                .build());
  }
}
