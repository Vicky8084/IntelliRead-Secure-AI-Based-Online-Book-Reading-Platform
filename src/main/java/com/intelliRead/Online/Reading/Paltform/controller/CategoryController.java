package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.Category;
import com.intelliRead.Online.Reading.Paltform.requestDTO.CategoryRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category/apies")
public class CategoryController {

    CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveCategory(@RequestBody CategoryRequestDTO categoryRequestDTO){
        try {
            String response = categoryService.addCategory(categoryRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findCategoryById(@PathVariable int id){
        try {
            Category category = categoryService.findCategoryById(id);
            if (category != null) {
                return ResponseEntity.ok(category);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Category>> findAllCategory(){
        try {
            List<Category> categories = categoryService.findAllCategory();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ NEW: Get main categories
    @GetMapping("/main")
    public ResponseEntity<List<Category>> getMainCategories(){
        try {
            List<Category> categories = categoryService.findMainCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ NEW: Get subcategories
    @GetMapping("/subcategories/{parentId}")
    public ResponseEntity<List<Category>> getSubCategories(@PathVariable int parentId){
        try {
            List<Category> categories = categoryService.findSubCategories(parentId);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ NEW: Get popular categories
    @GetMapping("/popular")
    public ResponseEntity<List<Category>> getPopularCategories(){
        try {
            List<Category> categories = categoryService.findAllCategory(); // Will enhance later
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable int id, @RequestBody CategoryRequestDTO categoryRequestDTO){
        try {
            String response = categoryService.updateCategory(id, categoryRequestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable int id){
        try {
            String response = categoryService.deleteCategory(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}