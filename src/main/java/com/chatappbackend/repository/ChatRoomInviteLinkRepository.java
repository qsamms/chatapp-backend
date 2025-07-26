package com.chatappbackend.repository;

import com.chatappbackend.models.ChatRoomInviteLink;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomInviteLinkRepository extends JpaRepository<ChatRoomInviteLink, UUID> {}
