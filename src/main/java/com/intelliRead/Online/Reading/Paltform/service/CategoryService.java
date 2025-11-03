package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.CategoryConverter;
import com.intelliRead.Online.Reading.Paltform.exception.CategoryAlreadyExistException;
import com.intelliRead.Online.Reading.Paltform.model.Category;
import com.intelliRead.Online.Reading.Paltform.repository.CategoryRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.CategoryRequestDTO;
//import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public String addCategory(CategoryRequestDTO categoryRequestDTO){
        Optional<Category> existingCategory = categoryRepository.findByCategoryName(categoryRequestDTO.getCategoryName());
        if (existingCategory.isPresent()) {
            throw new CategoryAlreadyExistException("Category '" + categoryRequestDTO.getCategoryName() + "' already exists!");
        }

        Category category = CategoryConverter.convertCategoryRequestDtoIntoCategory(categoryRequestDTO);

        // Set parent category if present
        if (categoryRequestDTO.getParentCategoryId() != null) {
            Category parentCategory = findCategoryById(categoryRequestDTO.getParentCategoryId());
            if (parentCategory != null) {
                category.setParentCategory(parentCategory);
            }
        }

        categoryRepository.save(category);
        return "Category Saved Successfully";
    }

    public Category findCategoryById(int id){
        return categoryRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Category> findAllCategory(){
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Category> findMainCategories() {
        return categoryRepository.findByParentCategoryIsNull();
    }

    @Transactional(readOnly = true)
    public List<Category> findSubCategories(int parentId) {
        return categoryRepository.findByParentCategoryId(parentId);
    }

    public String updateCategory(int id, CategoryRequestDTO categoryRequestDTO){
        Category category = findCategoryById(id);
        if (category == null) {
            return "Category not found";
        }

        category.setCategoryName(categoryRequestDTO.getCategoryName());
        category.setDescription(categoryRequestDTO.getDescription());

        if (categoryRequestDTO.getParentCategoryId() != null) {
            Category parentCategory = findCategoryById(categoryRequestDTO.getParentCategoryId());
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }

        categoryRepository.save(category);
        return "Category Updated Successfully";
    }

    public String deleteCategory(int id){
        Category category = findCategoryById(id);
        if (category == null) {
            return "Category Not found";
        }

        if (!category.getSubCategories().isEmpty()) {
            return "Cannot delete category. It has subcategories. Delete them first.";
        }

        if (!category.getBooks().isEmpty()) {
            return "Cannot delete category. It has books assigned. Reassign books first.";
        }

        categoryRepository.deleteById(id);
        return "Category deleted Successfully";
    }
}
