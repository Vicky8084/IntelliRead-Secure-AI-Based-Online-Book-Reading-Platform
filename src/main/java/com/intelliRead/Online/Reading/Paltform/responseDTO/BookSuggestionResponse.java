package com.intelliRead.Online.Reading.Paltform.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSuggestionResponse {
    private boolean success;
    private String message;
    private com.intelliRead.Online.Reading.Paltform.requestDTO.BookSuggestionDTO suggestion;
    private List<com.intelliRead.Online.Reading.Paltform.requestDTO.BookSuggestionDTO> suggestions;
    private Map<String, Object> stats;

    public BookSuggestionResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public BookSuggestionResponse(boolean success, String message, com.intelliRead.Online.Reading.Paltform.requestDTO.BookSuggestionDTO suggestion) {
        this.success = success;
        this.message = message;
        this.suggestion = suggestion;
    }
}