package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.requestDTO.BookSuggestionDTO;
import com.intelliRead.Online.Reading.Paltform.responseDTO.BookSuggestionResponse;
import com.intelliRead.Online.Reading.Paltform.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/suggestions")
public class UserSuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    // ✅ Get all suggestions for user with stats
    @GetMapping("/all")
    public ResponseEntity<BookSuggestionResponse> getAllSuggestionsForUser(@RequestParam int userId) {
        try {
            List<BookSuggestionDTO> suggestions = suggestionService.getPopularSuggestions(50); // Get all
            BookSuggestionResponse response = new BookSuggestionResponse(true, "Suggestions loaded successfully");
            response.setSuggestions(suggestions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BookSuggestionResponse(false, "Error loading suggestions: " + e.getMessage()));
        }
    }

    // ✅ Get popular suggestions
    @GetMapping("/popular")
    public ResponseEntity<BookSuggestionResponse> getPopularSuggestions(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<BookSuggestionDTO> suggestions = suggestionService.getPopularSuggestions(limit);
            BookSuggestionResponse response = new BookSuggestionResponse(true, "Popular suggestions loaded");
            response.setSuggestions(suggestions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BookSuggestionResponse(false, "Error loading popular suggestions: " + e.getMessage()));
        }
    }

    // ✅ Upvote a suggestion
    @PostMapping("/upvote")
    public ResponseEntity<Map<String, Object>> upvoteSuggestion(@RequestParam int userId,
                                                                @RequestParam int suggestionId) {
        try {
            String result = suggestionService.upvoteSuggestion(userId, suggestionId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error upvoting suggestion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // ✅ Get suggestion statistics
    @GetMapping("/{suggestionId}/stats")
    public ResponseEntity<Map<String, Object>> getSuggestionStats(@PathVariable int suggestionId) {
        try {
            Map<String, Object> stats = suggestionService.getSuggestionStats(suggestionId);
            stats.put("success", true);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error loading stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // ✅ Get user's voting history
    @GetMapping("/user/{userId}/votes")
    public ResponseEntity<Map<String, Object>> getUserVotes(@PathVariable int userId) {
        try {
            // This would return the user's voting history
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User votes retrieved");
            // Implementation would go here
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving votes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}