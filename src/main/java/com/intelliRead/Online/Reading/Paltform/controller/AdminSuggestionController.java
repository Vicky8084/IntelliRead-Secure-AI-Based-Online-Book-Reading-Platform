package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
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
@RequestMapping("/admin/suggestions")
public class AdminSuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getSuggestionAnalytics() {
        try {
            Map<String, Object> analytics = new HashMap<>();
            List<Suggestion> allSuggestions = suggestionService.findAllSuggestion();
            analytics.put("totalSuggestions", allSuggestions.size());
            analytics.put("pendingSuggestions", suggestionService.findPendingSuggestions().size());
            analytics.put("approvedSuggestions", suggestionService.findApprovedSuggestions().size());
            analytics.put("rejectedSuggestions", suggestionService.findRejectedSuggestions().size());
            int totalUpvotes = 0;
            int totalPublisherInterests = 0;
            for (Suggestion suggestion : allSuggestions) {
                Map<String, Object> stats = suggestionService.getSuggestionStats(suggestion.getId());
                totalUpvotes += (int) stats.get("upvoteCount");
                totalPublisherInterests += (int) stats.get("publisherInterestCount");
            }
            analytics.put("totalUpvotes", totalUpvotes);
            analytics.put("totalPublisherInterests", totalPublisherInterests);
            analytics.put("averageEngagement", allSuggestions.isEmpty() ? 0 : (totalUpvotes + totalPublisherInterests) / allSuggestions.size());
            analytics.put("success", true);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error loading analytics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/success-stories")
    public ResponseEntity<Map<String, Object>> getSuccessStories() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Success stories retrieved");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error loading success stories: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/bulk-actions")
    public ResponseEntity<Map<String, Object>> bulkActions(@RequestBody Map<String, Object> request) {
        try {
            List<Integer> suggestionIds = (List<Integer>) request.get("suggestionIds");
            String action = (String) request.get("action");
            String notes = (String) request.get("notes");
            int successCount = 0;
            for (int suggestionId : suggestionIds) {
                try {
                    if ("approve".equals(action)) {
                        suggestionService.approveSuggestion(suggestionId, notes);
                    } else if ("reject".equals(action)) {
                        suggestionService.rejectSuggestion(suggestionId, notes);
                    }
                    successCount++;
                } catch (Exception e) {
                }
            }
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Processed " + successCount + " out of " + suggestionIds.size() + " suggestions");
            response.put("processedCount", successCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error performing bulk actions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}