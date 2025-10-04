package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.CategoryConverter;
import com.intelliRead.Online.Reading.Paltform.model.Category;
import com.intelliRead.Online.Reading.Paltform.repository.CategoryRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.CategoryRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    CategoryRepository categoryRepository;
    @Autowired
    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository=categoryRepository;
    }

    public String addCategory(CategoryRequestDTO categoryRequestDTO){
        Category category= CategoryConverter.convertCategoryRequestDtoIntoCategory(categoryRequestDTO);
        categoryRepository.save(category);
        return "Category Saved";
    }
}
