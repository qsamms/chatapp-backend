package com.chatappbackend.ws;

import com.chatappbackend.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

  private final JwtUtil jwtUtil;

  public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  public String extractToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token == null || token.isEmpty()) {
      token = request.getParameter("token");
    }
    return token;
  }

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes) {
    if (request instanceof ServletServerHttpRequest servletRequest) {
      HttpServletRequest httpRequest = servletRequest.getServletRequest();
      String token = extractToken(httpRequest);
      if (token != null) {
        String username = jwtUtil.extractUsername(token);
        if (jwtUtil.validateToken(token, username)) {
          Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
          attributes.put("principal", authentication);
          return true;
        }
      }
    }
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    return false;
  }

  @Override
  public void afterHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Exception ex) {}
}
