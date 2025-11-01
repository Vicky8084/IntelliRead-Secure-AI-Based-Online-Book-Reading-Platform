package com.intelliRead.Online.Reading.Paltform.converter;

import com.intelliRead.Online.Reading.Paltform.model.Category;
import com.intelliRead.Online.Reading.Paltform.requestDTO.CategoryRequestDTO;

public class CategoryConverter {
    public static Category convertCategoryRequestDtoIntoCategory(CategoryRequestDTO categoryRequestDTO){
        Category category=new Category();
        category.setCategoryName(categoryRequestDTO.getCategoryName());
        return category;
    }
}
