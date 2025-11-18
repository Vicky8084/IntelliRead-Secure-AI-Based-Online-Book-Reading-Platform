package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.responseDTO.BookSuggestionResponse;
import com.intelliRead.Online.Reading.Paltform.requestDTO.PublisherSuggestionView;
import com.intelliRead.Online.Reading.Paltform.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/publisher/suggestions")
public class PublisherSuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    // ✅ Get all suggestions for publishers
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllSuggestionsForPublisher(@RequestParam int publisherId) {
        try {
            List<PublisherSuggestionView> suggestions = suggestionService.getSuggestionsForPublishers(publisherId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("suggestions", suggestions);
            response.put("count", suggestions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error loading suggestions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ✅ Express interest in a suggestion
    @PostMapping("/interest")
    public ResponseEntity<Map<String, Object>> expressInterest(@RequestParam int publisherId,
                                                               @RequestParam int suggestionId,
                                                               @RequestParam(required = false) String notes) {
        try {
            String result = suggestionService.expressInterest(publisherId, suggestionId, notes);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error expressing interest: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // ✅ Upload book for a suggestion
    @PostMapping("/upload-book")
    public ResponseEntity<Map<String, Object>> uploadBookForSuggestion(@RequestParam int publisherId,
                                                                       @RequestParam int suggestionId,
                                                                       @RequestParam int bookId,
                                                                       @RequestParam(required = false) String notes) {
        try {
            String result = suggestionService.uploadBookForSuggestion(publisherId, suggestionId, bookId, notes);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error uploading book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // ✅ Get publisher's action history
    @GetMapping("/publisher/{publisherId}/actions")
    public ResponseEntity<Map<String, Object>> getPublisherActions(@PathVariable int publisherId) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Publisher actions retrieved");
            // Implementation would return publisher's action history
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving actions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // ✅ Get suggestions with high engagement
    @GetMapping("/trending")
    public ResponseEntity<Map<String, Object>> getTrendingSuggestions(@RequestParam int publisherId) {
        try {
            List<PublisherSuggestionView> suggestions = suggestionService.getSuggestionsForPublishers(publisherId);

            // Filter and sort by engagement
            suggestions.sort((s1, s2) -> Integer.compare(
                    s2.getTotalUpvotes() + s2.getTotalPublisherInterests(),
                    s1.getTotalUpvotes() + s1.getTotalPublisherInterests()
            ));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("suggestions", suggestions.stream().limit(10).collect(Collectors.toList()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error loading trending suggestions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}