package com.chatappbackend.service;

import com.chatappbackend.models.User;
import com.chatappbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User createUser(String username, String rawPassword, String email) {
    User user =
        User.builder()
            .username(username)
            .password(passwordEncoder.encode(rawPassword))
            .email(email)
            .enabled(true)
            .build();
    return userRepository.save(user);
  }
}
