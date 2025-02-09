package com.chatappbackend.views;

import com.chatappbackend.dto.AuthRequest;
import com.chatappbackend.dto.SignUpRequest;
import com.chatappbackend.dto.SignUpResponse;
import com.chatappbackend.models.User;
import com.chatappbackend.repository.UserRepository;
import com.chatappbackend.service.CustomUserDetailsService;
import com.chatappbackend.utils.JwtUtil;
import com.chatappbackend.utils.MapUtil;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  public AuthController(
      AuthenticationManager authManager,
      JwtUtil jwtUtil,
      CustomUserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder,
      UserRepository userRepository) {
    this.authenticationManager = authManager;
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
    String token = jwtUtil.generateToken(userDetails.getUsername());
    return ResponseEntity.ok(Map.of("token", token));
  }

  @PostMapping("/signup")
  public ResponseEntity<Map<String, Object>> signup(@RequestBody SignUpRequest request) {
    try {
      User user =
          User.builder()
              .username(request.getUsername())
              .password(passwordEncoder.encode(request.getPassword()))
              .email(request.getEmail())
              .enabled(true)
              .build();
      User savedUser = userRepository.save(user);
      SignUpResponse response =
          SignUpResponse.builder()
              .username(savedUser.getUsername())
              .email(savedUser.getEmail())
              .build();
      return ResponseEntity.ok(MapUtil.toMap(response));
    } catch (DataIntegrityViolationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "Error creating user: " + e.getMessage()));
    }
  }
}
