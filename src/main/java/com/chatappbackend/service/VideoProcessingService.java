package com.chatappbackend.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class VideoProcessingService {
  @Async
  public void generateDash(File inputFile, UUID videoId) {
    File outputDir = new File("videos/" + videoId);
    outputDir.mkdirs();

    String outputPath = new File(outputDir, "stream.mpd").getAbsolutePath();
    String inputPath = inputFile.getAbsolutePath();

    ProcessBuilder pb =
        new ProcessBuilder(
            "ffmpeg",
            "-i",
            inputPath,
            "-map",
            "0",
            "-codec:v",
            "libx264",
            "-codec:a",
            "aac",
            "-f",
            "dash",
            "-seg_duration",
            "4",
            "-frag_duration",
            "2000",
            "-streaming",
            "1",
            "-use_timeline",
            "1",
            "-use_template",
            "1",
            outputPath);

    pb.redirectErrorStream(true);

    try {
      Process process = pb.start();
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        System.out.println("Video converted successfully to DASH: " + outputPath);
      } else {
        System.err.println("FFmpeg failed with exit code " + exitCode);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
