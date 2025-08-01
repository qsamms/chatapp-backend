package com.chatappbackend.ws;

import com.chatappbackend.utils.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtUtil jwtUtil;
  private final ChatRoomSubscriptionInterceptor chatRoomSubscriptionInterceptor;

  public WebSocketConfig(
      JwtUtil jwtUtil, ChatRoomSubscriptionInterceptor chatRoomSubscriptionInterceptor) {
    this.jwtUtil = jwtUtil;
    this.chatRoomSubscriptionInterceptor = chatRoomSubscriptionInterceptor;
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(chatRoomSubscriptionInterceptor);
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/ws")
        .setHandshakeHandler(new CustomHandshakeHandler())
        .addInterceptors(new JwtHandshakeInterceptor(jwtUtil))
        .setAllowedOriginPatterns("http://localhost:8081", "http://127.0.0.1:8081")
        .withSockJS();
  }
}
