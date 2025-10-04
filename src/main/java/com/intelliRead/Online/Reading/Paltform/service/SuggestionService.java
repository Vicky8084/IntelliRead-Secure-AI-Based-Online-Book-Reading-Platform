package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.SuggestionConverter;
import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
import com.intelliRead.Online.Reading.Paltform.repository.SuggestionRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.SuggestionRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuggestionService {
    SuggestionRepository suggestionRepository;
    @Autowired
    public SuggestionService(SuggestionRepository suggestionRepository){
        this.suggestionRepository=suggestionRepository;
    }

    public String saveSuggestion(SuggestionRequestDTO suggestionRequestDTO){
        Suggestion suggestion= SuggestionConverter.convertSuggestionRequestDtoIntoSuggestion(suggestionRequestDTO);
        suggestionRepository.save(suggestion);
        return "Suggestion Saved Successfully";
    }

    public Suggestion findSuggestionById(int id){
        Optional<Suggestion> suggestionOptional= suggestionRepository.findById(id);
        if(suggestionOptional.isPresent()){
            return suggestionOptional.get();
        }
        else {
            return null;
        }
    }

    public List<Suggestion> findAllSuggestion(){
        return suggestionRepository.findAll();
    }

    public String updateSuggestion(int id, SuggestionRequestDTO suggestionRequestDTO){
        Suggestion suggestion=findSuggestionById(id);
        if(suggestion!=null){
            suggestion.setSuggestedTitle(suggestionRequestDTO.getSuggestedTitle());
            suggestion.setAuthor(suggestionRequestDTO.getAuthor());
            suggestion.setStatus(suggestionRequestDTO.getStatus());
            suggestionRepository.save(suggestion);
            return "Suggestion Updated Successfully";
        }
        else{
            return "Suggestion not found";
        }
    }

    public String deleteSuggestion(int id){
        Suggestion suggestion=findSuggestionById(id);
        if(suggestion!=null){
            suggestionRepository.deleteById(id);
            return "Suggestion deleted Successfully";
        }else{
            return "suggestion not found";
        }
    }
}
