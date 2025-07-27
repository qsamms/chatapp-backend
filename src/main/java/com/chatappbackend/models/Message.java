package com.chatappbackend.models;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(
    name = "messages",
    uniqueConstraints = @UniqueConstraint(columnNames = {"chat_room_id", "sequence_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @ManyToOne
  @JoinColumn(nullable = false)
  private User sender;

  @ManyToOne
  @JoinColumn(nullable = false)
  private ChatRoom chatRoom;

  @Column(nullable = false)
  private Long sequenceNumber;

  @Column(nullable = false)
  @Builder.Default
  private Instant timestamp = Instant.now();

  @Column(nullable = true)
  private String mediaUrl;
}
