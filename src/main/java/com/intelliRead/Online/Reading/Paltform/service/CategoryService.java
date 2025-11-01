package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.CategoryConverter;
import com.intelliRead.Online.Reading.Paltform.exception.CategoryAlreadyExistException;
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
        this.categoryRepository = categoryRepository;
    }

    public String addCategory(CategoryRequestDTO categoryRequestDTO){
        // ✅ Check if category already exists
        Optional<Category> existingCategory = categoryRepository.findByCategoryName(categoryRequestDTO.getCategoryName());
        if (existingCategory.isPresent()) {
            throw new CategoryAlreadyExistException("Category '" + categoryRequestDTO.getCategoryName() + "' already exists!");
        }

        Category category = CategoryConverter.convertCategoryRequestDtoIntoCategory(categoryRequestDTO);

        // ✅ Handle parent category
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
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        return categoryOptional.orElse(null);
    }

    public List<Category> findAllCategory(){
        return categoryRepository.findAll();
    }

    // ✅ Get main categories (no parent)
    public List<Category> findMainCategories() {
        return categoryRepository.findByParentCategoryIsNull();
    }

    // ✅ Get subcategories by parent ID
    public List<Category> findSubCategories(int parentId) {
        return categoryRepository.findByParentCategoryId(parentId);
    }

    public String updateCategory(int id, CategoryRequestDTO categoryRequestDTO){
        Category category = findCategoryById(id);
        if (category != null) {
            category.setCategoryName(categoryRequestDTO.getCategoryName());
            category.setDescription(categoryRequestDTO.getDescription());

            // ✅ Update parent category
            if (categoryRequestDTO.getParentCategoryId() != null) {
                Category parentCategory = findCategoryById(categoryRequestDTO.getParentCategoryId());
                category.setParentCategory(parentCategory);
            } else {
                category.setParentCategory(null);
            }

            categoryRepository.save(category);
            return "Category Updated Successfully";
        } else {
            return "Category not found";
        }
    }

    public String deleteCategory(int id){
        Category category = findCategoryById(id);
        if (category != null) {
            // ✅ Check if category has subcategories
            if (!category.getSubCategories().isEmpty()) {
                return "Cannot delete category. It has subcategories. Delete subcategories first.";
            }

            // ✅ Check if category has books
            if (!category.getBooks().isEmpty()) {
                return "Cannot delete category. It has books assigned. Reassign books first.";
            }

            categoryRepository.deleteById(id);
            return "Category deleted Successfully";
        } else {
            return "Category Not found";
        }
    }
}