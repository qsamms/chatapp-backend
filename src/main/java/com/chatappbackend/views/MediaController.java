package com.chatappbackend.views;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
public class MediaController {
  private static final String UPLOAD_DIR = "uploads";

  @PostMapping("/upload/")
  public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file)
      throws IOException {
    if (file.isEmpty()) return ResponseEntity.badRequest().body("Empty file");

    File uploadDir = new File(UPLOAD_DIR);
    if (!uploadDir.exists()) uploadDir.mkdirs();

    String filename = file.getOriginalFilename();
    Path filepath = Paths.get(UPLOAD_DIR, filename);
    Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

    return ResponseEntity.ok("/" + filename);
  }
}
