package com.chatappbackend.repository;

import com.chatappbackend.models.ChatRoomInviteLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRoomInviteLinkRepository extends JpaRepository<ChatRoomInviteLink, UUID> {
}
