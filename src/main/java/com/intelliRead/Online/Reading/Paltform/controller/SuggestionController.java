package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
import com.intelliRead.Online.Reading.Paltform.requestDTO.SuggestionRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suggestion/apis")
public class SuggestionController {
    SuggestionService suggestionService;

    @Autowired
    public SuggestionController(SuggestionService suggestionService){
        this.suggestionService = suggestionService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveSuggestion(@RequestBody SuggestionRequestDTO suggestionRequestDTO){
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
    public ResponseEntity<List<Suggestion>> findAllSuggestion(){
        try {
            List<Suggestion> suggestions = suggestionService.findAllSuggestion();
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateSuggestion(@PathVariable int id, @RequestBody SuggestionRequestDTO suggestionRequestDTO){
        try {
            String response = suggestionService.updateSuggestion(id, suggestionRequestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSuggestion(@PathVariable int id){
        try {
            String response = suggestionService.deleteSuggestion(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}