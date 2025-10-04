package com.intelliRead.Online.Reading.Paltform.converter;

import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
import com.intelliRead.Online.Reading.Paltform.requestDTO.SuggestionRequestDTO;

public class SuggestionConverter {
    public static Suggestion convertSuggestionRequestDtoIntoSuggestion(SuggestionRequestDTO suggestionRequestDTO){
        Suggestion suggestion=new Suggestion();
        suggestion.setSuggestedTitle(suggestionRequestDTO.getSuggestedTitle());
        suggestion.setAuthor(suggestionRequestDTO.getAuthor());
        suggestion.setStatus(suggestionRequestDTO.getStatus());
        return suggestion;
    }
}

