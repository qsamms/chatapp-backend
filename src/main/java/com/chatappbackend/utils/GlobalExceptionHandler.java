package com.chatappbackend.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, ArrayList<String>>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, ArrayList<String>> errors = new HashMap<>();
    errors.put("errors", new ArrayList<>());
    ArrayList<String> errorsList = errors.get("errors");

    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errorsList.add(error.getDefaultMessage());
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, String>> handleDataIntegrityException(Exception ex) {
    class Utils {
      public static String sanitizeDataIntegrityViolation(String message) {
        if (message.contains("already exists")) {
          String key = extractKey(message);
          return "A user with that " + key + " already exists";
        }
        return "Error creating user";
      }

      private static String extractKey(String message) {
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)=\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
          return matcher.group(1);
        }
        return null;
      }
    }
    Map<String, String> response = new HashMap<>();
    response.put("error", Utils.sanitizeDataIntegrityViolation(ex.getMessage()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, String>> handleAuthenticationException(Exception ex) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("error", "An unexpected error occurred");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
