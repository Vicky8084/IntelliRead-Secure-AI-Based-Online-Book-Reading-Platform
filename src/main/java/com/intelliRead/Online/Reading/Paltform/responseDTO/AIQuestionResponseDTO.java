package com.intelliRead.Online.Reading.Paltform.responseDTO;

import lombok.Data;

@Data
public class AIQuestionResponseDTO {
    private String answer;
    private String[] relevantSections;
    private boolean answeredFromContext;
    private boolean success;
    private String errorMessage;
}