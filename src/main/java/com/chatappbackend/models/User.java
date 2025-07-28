package com.chatappbackend.models;

import com.chatappbackend.dto.user.Role;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Set;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String email;

  @Column private String displayName;

  @Column(columnDefinition = "TEXT")
  private String bio;

  @Enumerated(EnumType.STRING)
  private Role role;

  @Column(nullable = false)
  @Builder.Default
  private boolean enabled = true;

  @Column
  private Instant lastHeartbeat;

  @Column(nullable = false, columnDefinition = "boolean default true")
  @Builder.Default
  private boolean displayActiveStatus = true;

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public Set<GrantedAuthority> getAuthorities() {
    return Set.of();
  }
}
