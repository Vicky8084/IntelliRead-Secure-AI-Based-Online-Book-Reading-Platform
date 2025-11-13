package com.intelliRead.Online.Reading.Paltform.requestDTO;

import lombok.Data;

@Data
public class AIQuestionRequestDTO {
    private String question;
    private String context; // Book content or chapter content
    private int bookId;
    private String language;
}