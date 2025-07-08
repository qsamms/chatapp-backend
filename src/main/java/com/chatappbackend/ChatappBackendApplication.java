package com.chatappbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ChatappBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChatappBackendApplication.class, args);
  }
}
