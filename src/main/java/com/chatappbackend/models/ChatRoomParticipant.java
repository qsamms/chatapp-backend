package com.chatappbackend.models;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "chatroomparticipants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomParticipant {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(nullable = false)
  private ChatRoom chatRoom;

  @Column private Instant joinedAt;

  @Column(nullable = false)
  @Builder.Default
  private Boolean hasAccepted = false;
}
