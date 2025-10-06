package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.SuggestionConverter;
import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.SuggestionRepository;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.SuggestionRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuggestionService {
    SuggestionRepository suggestionRepository;
    UserRepository userRepository;
    @Autowired
    public SuggestionService(SuggestionRepository suggestionRepository,
                             UserRepository userRepository){
        this.suggestionRepository=suggestionRepository;
        this.userRepository=userRepository;
    }

    public String saveSuggestion(SuggestionRequestDTO suggestionRequestDTO){
        Suggestion suggestion= SuggestionConverter.convertSuggestionRequestDtoIntoSuggestion(suggestionRequestDTO);
        User user=userRepository.findById(suggestionRequestDTO.getUserId()).get();
        suggestion.setUser(user);
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
