package com.intelliRead.Online.Reading.Paltform.responseDTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookWithCategoryDTO {
    private int id;
    private String title;
    private String author;
    private String description;
    private String language;
    private String status;
    private String categoryName;
    private Integer categoryId;

    // constructors, getters, setters
}
