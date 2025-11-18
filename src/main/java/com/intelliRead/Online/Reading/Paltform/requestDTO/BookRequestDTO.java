package com.intelliRead.Online.Reading.Paltform.requestDTO;

import com.intelliRead.Online.Reading.Paltform.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDTO {
    private String title;
    private String author;
    private String description;
    private String language;
    private int userId;
    private Integer categoryId; // ✅ ADDED: For category assignment
    private BookStatus status = BookStatus.PENDING;
    private Integer suggestionId;
}