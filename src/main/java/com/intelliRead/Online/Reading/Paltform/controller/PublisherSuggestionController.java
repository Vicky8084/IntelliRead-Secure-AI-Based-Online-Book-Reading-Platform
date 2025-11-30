package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.requestDTO.PublisherSuggestionView;
import com.intelliRead.Online.Reading.Paltform.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/suggestion")
public class PublisherSuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllSuggestions() {
        try {
            List<PublisherSuggestionView> suggestions = suggestionService.getAllSuggestionsForPublishers();
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

    @GetMapping("/publisher/{publisherId}")
    public ResponseEntity<Map<String, Object>> getSuggestionsForPublisher(@PathVariable int publisherId) {
        try {
            List<PublisherSuggestionView> suggestions = suggestionService.getSuggestionsForPublisher(publisherId);
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

    @PostMapping("/interest")
    public ResponseEntity<Map<String, Object>> expressInterest(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("🎯 Express interest request received: " + request);
            Integer publisherId = null;
            Integer suggestionId = null;
            if (request.get("publisherId") != null) {
                publisherId = Integer.valueOf(request.get("publisherId").toString());
            }
            if (request.get("suggestionId") != null) {
                suggestionId = Integer.valueOf(request.get("suggestionId").toString());
            }
            String notes = request.get("notes") != null ? request.get("notes").toString() : "No notes provided";
            if (publisherId == null || suggestionId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Publisher ID and Suggestion ID are required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            System.out.println("🔍 Processing interest - Publisher: " + publisherId + ", Suggestion: " + suggestionId);
            String result = suggestionService.expressInterest(publisherId, suggestionId, notes);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Error expressing interest: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error expressing interest: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadBookForSuggestion(@RequestBody Map<String, Object> request) {
        try {
            int publisherId = (int) request.get("publisherId");
            int suggestionId = (int) request.get("suggestionId");
            String result = suggestionService.markSuggestionAsUploaded(publisherId, suggestionId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error marking as uploaded: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/{suggestionId}")
    public ResponseEntity<Map<String, Object>> getSuggestionDetails(@PathVariable("suggestionId") Integer suggestionId) {
        try {
            System.out.println("🔍 Get suggestion details request: " + suggestionId);
            if (suggestionId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Suggestion ID is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            PublisherSuggestionView suggestion = suggestionService.getSuggestionDetails(suggestionId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("suggestion", suggestion);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Error loading suggestion details: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error loading suggestion details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}