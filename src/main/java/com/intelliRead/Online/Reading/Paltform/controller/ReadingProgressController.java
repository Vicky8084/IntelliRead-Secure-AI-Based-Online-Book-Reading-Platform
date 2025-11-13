package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.ReadingProgress;
import com.intelliRead.Online.Reading.Paltform.service.ReadingProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/progress")
public class ReadingProgressController {

    @Autowired
    private ReadingProgressService readingProgressService;

    @PostMapping("/update")
    public ResponseEntity<?> updateReadingProgress(@RequestBody Map<String, Object> request) {
        try {
            int userId = (Integer) request.get("userId");
            int bookId = (Integer) request.get("bookId");
            int currentPage = (Integer) request.get("currentPage");
            int totalPages = (Integer) request.get("totalPages");
            int readingTimeMinutes = (Integer) request.get("readingTimeMinutes");

            ReadingProgress progress = readingProgressService.updateReadingProgress(
                    userId, bookId, currentPage, totalPages, readingTimeMinutes);

            return ResponseEntity.ok(progress);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/book/{bookId}")
    public ResponseEntity<?> getReadingProgress(
            @PathVariable int userId,
            @PathVariable int bookId) {
        try {
            ReadingProgress progress = readingProgressService.getReadingProgress(userId, bookId);
            if (progress != null) {
                return ResponseEntity.ok(progress);
            } else {
                return ResponseEntity.ok().body(Map.of("message", "No progress found"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/position")
    public ResponseEntity<?> saveLastPosition(@RequestBody Map<String, Object> request) {
        try {
            int userId = (Integer) request.get("userId");
            int bookId = (Integer) request.get("bookId");
            String positionData = (String) request.get("positionData");

            String result = readingProgressService.saveLastPosition(userId, bookId, positionData);
            return ResponseEntity.ok().body(Map.of("message", result));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}