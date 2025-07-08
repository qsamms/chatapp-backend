package com.chatappbackend.views;

import com.chatappbackend.service.VideoProcessingService;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
public class MediaController {
  private static final String UPLOAD_DIR = "uploads";

  private final VideoProcessingService videoProcessingService;

  public MediaController(VideoProcessingService videoProcessingService) {
    this.videoProcessingService = videoProcessingService;
  }

  @PostMapping("/upload/")
  public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file)
      throws IOException {
    if (file.isEmpty()) return ResponseEntity.badRequest().body("Empty file");

    File uploadDir = new File(UPLOAD_DIR);
    if (!uploadDir.exists()) uploadDir.mkdirs();

    String filename = file.getOriginalFilename();
    if (filename != null && filename.matches("(?i).+\\.(mp4|jpeg|jpg|png)$")) {
      UUID mediaId = UUID.randomUUID();
      String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
      String newFile = mediaId + "." + extension;
      Path mediaPath = Paths.get(UPLOAD_DIR, newFile);
      Files.copy(file.getInputStream(), mediaPath, StandardCopyOption.REPLACE_EXISTING);

      String contentType = file.getContentType();
      if (contentType != null && contentType.startsWith("video/")) {
        videoProcessingService.generateDash(new File(String.valueOf(mediaPath)), mediaId);
        return ResponseEntity.ok("videos/" + mediaId + "/stream.mpd");
      }
      return ResponseEntity.ok("uploads/" + newFile);
    } else {
      return ResponseEntity.badRequest().body("Unsupported media type");
    }
  }
}
