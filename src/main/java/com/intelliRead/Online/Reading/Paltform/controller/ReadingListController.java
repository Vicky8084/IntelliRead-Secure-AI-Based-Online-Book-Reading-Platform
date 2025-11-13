package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.ReadingList;
import com.intelliRead.Online.Reading.Paltform.service.ReadingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reading")
public class ReadingListController {

    @Autowired
    private ReadingListService readingListService;

    @PostMapping("/add")
    public ResponseEntity<?> addToReadingList(@RequestBody Map<String, Integer> request) {
        try {
            int userId = request.get("userId");
            int bookId = request.get("bookId");

            String result = readingListService.addToReadingList(userId, bookId);
            return ResponseEntity.ok().body(Map.of("message", result));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromReadingList(@RequestBody Map<String, Integer> request) {
        try {
            int userId = request.get("userId");
            int bookId = request.get("bookId");

            String result = readingListService.removeFromReadingList(userId, bookId);
            return ResponseEntity.ok().body(Map.of("message", result));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/status")
    public ResponseEntity<?> updateReadingStatus(@RequestBody Map<String, Object> request) {
        try {
            int userId = (Integer) request.get("userId");
            int bookId = (Integer) request.get("bookId");
            ReadingList.ReadingStatus status = ReadingList.ReadingStatus.valueOf((String) request.get("status"));

            String result = readingListService.updateReadingStatus(userId, bookId, status);
            return ResponseEntity.ok().body(Map.of("message", result));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReadingList(@PathVariable int userId) {
        try {
            List<ReadingList> readingList = readingListService.getUserReadingList(userId);
            return ResponseEntity.ok(readingList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<?> getUserReadingListByStatus(
            @PathVariable int userId,
            @PathVariable String status) {
        try {
            ReadingList.ReadingStatus readingStatus = ReadingList.ReadingStatus.valueOf(status.toUpperCase());
            List<ReadingList> readingList = readingListService.getUserReadingListByStatus(userId, readingStatus);
            return ResponseEntity.ok(readingList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkBookInReadingList(
            @RequestParam int userId,
            @RequestParam int bookId) {
        try {
            boolean exists = readingListService.isBookInReadingList(userId, bookId);
            return ResponseEntity.ok().body(Map.of("inReadingList", exists));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}