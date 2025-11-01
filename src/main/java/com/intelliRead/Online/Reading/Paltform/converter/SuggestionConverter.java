package com.intelliRead.Online.Reading.Paltform.converter;

import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
import com.intelliRead.Online.Reading.Paltform.requestDTO.SuggestionRequestDTO;

public class SuggestionConverter {
    public static Suggestion convertSuggestionRequestDtoIntoSuggestion(SuggestionRequestDTO suggestionRequestDTO){
        Suggestion suggestion = new Suggestion();
        suggestion.setSuggestedTitle(suggestionRequestDTO.getSuggestedTitle());
        suggestion.setAuthor(suggestionRequestDTO.getAuthor());

        // ✅ Only set status if provided, otherwise keep default
        if (suggestionRequestDTO.getSuggestionStatus() != null) {
            suggestion.setSuggestionStatus(suggestionRequestDTO.getSuggestionStatus());
        }
        return suggestion;
    }
}

