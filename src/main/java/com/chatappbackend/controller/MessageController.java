package com.chatappbackend.controller;

import com.chatappbackend.document.MessageDocument;
import com.chatappbackend.dto.message.DeleteMessageReq;
import com.chatappbackend.models.Message;
import com.chatappbackend.models.User;
import com.chatappbackend.service.ChatService;
import com.chatappbackend.service.ElasticService;
import com.chatappbackend.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {

  @Autowired private UserService userService;
  @Autowired private ChatService chatService;
  @Autowired private ElasticService elasticService;

  @DeleteMapping
  public ResponseEntity<?> deleteMessage(
      @Valid @RequestBody DeleteMessageReq deleteMessageReq, Principal principal) {
    User user = userService.getUser(principal.getName());

    Message message = chatService.getMessage(UUID.fromString(deleteMessageReq.getMessageId()));

    if (!message.getSender().getId().equals(user.getId())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of());
    }

    chatService.deleteMessage(message.getId());
    elasticService.removeFromIndex(new MessageDocument(message));

    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of());
  }
}
