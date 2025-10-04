package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
import com.intelliRead.Online.Reading.Paltform.requestDTO.SuggestionRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Suggestion/apis")
public class SuggestionController {
    SuggestionService suggestionService;
    @Autowired
    public SuggestionController(SuggestionService suggestionService){
        this.suggestionService=suggestionService;
    }

    @PostMapping("/save")
    public String saveSuggestion(@RequestBody SuggestionRequestDTO suggestionRequestDTO){
        return suggestionService.saveSuggestion(suggestionRequestDTO);
    }

    @GetMapping("/findById/{id}")
    public Suggestion findSuggestionById(@PathVariable int id) {
        return suggestionService.findSuggestionById(id);
    }

    @GetMapping("/findAll")
    public List<Suggestion> findAllSuggestion(){
        return suggestionService.findAllSuggestion();
    }

    @PutMapping("/update/id")
    public String updateSuggestion(@PathVariable int id, SuggestionRequestDTO suggestionRequestDTO){
        return suggestionService.updateSuggestion(id,suggestionRequestDTO);
    }
}
