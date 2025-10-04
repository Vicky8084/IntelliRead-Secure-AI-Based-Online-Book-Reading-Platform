package com.intelliRead.Online.Reading.Paltform.requestDTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDTO {

    private String title;
    private String author;
    private String Description;
    private String language;
}
