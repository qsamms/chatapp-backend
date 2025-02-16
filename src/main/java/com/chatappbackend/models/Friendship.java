package com.chatappbackend.models;

import com.chatappbackend.dto.FriendshipStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "friendships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id1", nullable = false)
  private User user1;

  @ManyToOne
  @JoinColumn(name = "user_id2", nullable = false)
  private User user2;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FriendshipStatus friendshipStatus;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @PreUpdate
  public void setUpdatedAt() {
    this.updatedAt = LocalDateTime.now();
  }
}
