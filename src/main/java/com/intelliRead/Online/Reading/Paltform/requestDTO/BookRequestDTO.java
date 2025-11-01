package com.intelliRead.Online.Reading.Paltform.requestDTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDTO {

    private String title;
    private String author;
    private String description;
    private String language;
    private int userId;
}
