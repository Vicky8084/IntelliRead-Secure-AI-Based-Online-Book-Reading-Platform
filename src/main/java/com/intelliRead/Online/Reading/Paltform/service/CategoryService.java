package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.CategoryConverter;
import com.intelliRead.Online.Reading.Paltform.model.Category;
import com.intelliRead.Online.Reading.Paltform.repository.CategoryRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.CategoryRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Category findCategoryById(int id){
        Optional<Category> categoryOptional=categoryRepository.findById(id);
        if(categoryOptional!=null){
            return categoryOptional.get();
        }
        else{
            return null;
        }
    }

    public List<Category> findAllCategory(){
        return categoryRepository.findAll();
    }

    public String updateCategory(int id, CategoryRequestDTO categoryRequestDTO){
        Category category=findCategoryById(id);
        category.setCategoryName(categoryRequestDTO.getCategoryName());
        categoryRepository.save(category);
        return "Category Updated Successfully";
    }

    public String deleteCategory(int id){
        Category category=findCategoryById(id);
        if(category!=null){
            categoryRepository.deleteById(id);
            return "Category deleted Successfully";
        }
        else {
            return "Category Not found";
        }
    }
}
