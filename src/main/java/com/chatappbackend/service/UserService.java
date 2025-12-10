package com.chatappbackend.service;

import com.chatappbackend.dto.user.Role;
import com.chatappbackend.models.User;
import com.chatappbackend.repository.UserRepository;
import java.util.List;
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

  public User createUser(String email, String rawPassword, String firstName, String lastName) {
    User user =
        User.builder()
            .username(email)
            .firstName(firstName)
            .lastName(lastName)
            .password(passwordEncoder.encode(rawPassword))
            .email(email)
            .role(Role.USER)
            .build();
    return userRepository.save(user);
  }

  public User getUser(String username) {
    return userDetailsService.loadUserByUsername(username);
  }

  public List<User> getUsersByIds(List<Long> userIds) {
    return userRepository.findByIdIn(userIds);
  }

  public User getUserByEmail(String email) {
    return userDetailsService.loadUserByEmail(email);
  }

  public User saveUser(User user) {
    return userRepository.save(user);
  }
}
