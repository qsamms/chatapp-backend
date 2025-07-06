package com.chatappbackend.service;

import com.chatappbackend.dto.user.Role;
import com.chatappbackend.models.User;
import com.chatappbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final CustomUserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;

  public UserService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      CustomUserDetailsService userDetailsService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userDetailsService = userDetailsService;
  }

  public User createUser(String username, String rawPassword, String email) {
    User user =
        User.builder()
            .username(username)
            .password(passwordEncoder.encode(rawPassword))
            .email(email)
            .role(Role.USER)
            .build();
    return userRepository.save(user);
  }

  public User getUser(String username) {
    return userDetailsService.loadUserByUsername(username);
  }
}
