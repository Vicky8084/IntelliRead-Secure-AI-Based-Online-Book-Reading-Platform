package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.service.ReadingAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private ReadingAnalyticsService readingAnalyticsService;

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<?> getUserReadingStats(@PathVariable int userId) {
        try {
            Map<String, Object> stats = readingAnalyticsService.getUserReadingStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<?> getRecentlyReadBooks(@PathVariable int userId) {
        try {
            return ResponseEntity.ok(readingAnalyticsService.getRecentlyReadBooks(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/nearly-completed")
    public ResponseEntity<?> getNearlyCompletedBooks(@PathVariable int userId) {
        try {
            return ResponseEntity.ok(readingAnalyticsService.getNearlyCompletedBooks(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}