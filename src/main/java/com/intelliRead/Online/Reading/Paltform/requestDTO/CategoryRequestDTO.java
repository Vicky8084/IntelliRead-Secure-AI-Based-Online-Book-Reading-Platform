package com.intelliRead.Online.Reading.Paltform.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDTO {
    private String categoryName;
    private String description;
    private Integer parentCategoryId; // ✅ For hierarchical categories
}