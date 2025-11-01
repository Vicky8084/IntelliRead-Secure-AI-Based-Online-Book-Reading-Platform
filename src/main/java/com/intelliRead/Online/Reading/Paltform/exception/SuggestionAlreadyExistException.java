package com.intelliRead.Online.Reading.Paltform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class SuggestionAlreadyExistException extends RuntimeException {
    public SuggestionAlreadyExistException(String message) {
        super(message);
    }
}