package com.intelliRead.Online.Reading.Paltform.responseDTO;

import lombok.Data;

@Data
public class AISummaryResponseDTO {
    private String summary;
    private String[] keyPoints;
    private String readingTime;
    private String difficultyLevel;
    private boolean success;
    private String errorMessage;
}