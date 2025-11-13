package com.intelliRead.Online.Reading.Paltform.requestDTO;

import lombok.Data;

@Data
public class AISummaryRequestDTO {
    private String content;
    private String type; // CHAPTER, BOOK, SECTION
    private int maxLength; // Summary length in words
    private String language; // en, hi, etc.
}