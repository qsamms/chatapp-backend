package com.example.chatapp_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

  @GetMapping("/ping")
  public String index() {
    return "pong!";
  }
}
