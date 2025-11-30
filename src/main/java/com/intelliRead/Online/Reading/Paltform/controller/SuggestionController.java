package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
import com.intelliRead.Online.Reading.Paltform.requestDTO.SuggestionRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/suggestion/apis")
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @PostMapping("/save")
    public ResponseEntity<String> saveSuggestion(@RequestBody SuggestionRequestDTO suggestionRequestDTO) {
        try {
            String response = suggestionService.saveSuggestion(suggestionRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findSuggestionById(@PathVariable int id) {
        try {
            Suggestion suggestion = suggestionService.findSuggestionById(id);
            if (suggestion != null) {
                return ResponseEntity.ok(suggestion);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Suggestion not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Suggestion>> findAllSuggestion() {
        try {
            List<Suggestion> suggestions = suggestionService.findAllSuggestion();
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Suggestion>> findSuggestionsByUserId(@PathVariable int userId) {
        try {
            List<Suggestion> suggestions = suggestionService.findSuggestionsByUserId(userId);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateSuggestion(@PathVariable int id, @RequestBody SuggestionRequestDTO suggestionRequestDTO) {
        try {
            String response = suggestionService.updateSuggestion(id, suggestionRequestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSuggestion(@PathVariable int id) {
        try {
            String response = suggestionService.deleteSuggestion(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Suggestion>> getAllSuggestionsForAdmin() {
        try {
            List<Suggestion> suggestions = suggestionService.findAllSuggestion();
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/admin/status/{status}")
    public ResponseEntity<List<Suggestion>> getSuggestionsByStatus(@PathVariable String status) {
        try {
            List<Suggestion> suggestions = suggestionService.getSuggestionsByStatus(status);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/admin/approve/{id}")
    public ResponseEntity<Map<String, Object>> approveSuggestion(@PathVariable int id, @RequestBody Map<String, String> request) {
        try {
            String adminNotes = request.get("adminNotes");
            String response = suggestionService.approveSuggestion(id, adminNotes);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("message", response);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("success", false);
            errorMap.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }
    }

    @PutMapping("/admin/reject/{id}")
    public ResponseEntity<Map<String, Object>> rejectSuggestion(@PathVariable int id, @RequestBody Map<String, String> request) {
        try {
            String adminNotes = request.get("adminNotes");
            String response = suggestionService.rejectSuggestion(id, adminNotes);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("message", response);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("success", false);
            errorMap.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Long>> getSuggestionStats() {
        try {
            long total = suggestionService.findAllSuggestion().size();
            long pending = suggestionService.findPendingSuggestions().size();
            long approved = suggestionService.findApprovedSuggestions().size();
            long rejected = suggestionService.findRejectedSuggestions().size();
            Map<String, Long> stats = new HashMap<>();
            stats.put("total", total);
            stats.put("pending", pending);
            stats.put("approved", approved);
            stats.put("rejected", rejected);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}