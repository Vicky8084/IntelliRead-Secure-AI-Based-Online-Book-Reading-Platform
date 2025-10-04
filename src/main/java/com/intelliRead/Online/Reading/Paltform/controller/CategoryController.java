package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.Category;
import com.intelliRead.Online.Reading.Paltform.requestDTO.CategoryRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category/apies")
public class CategoryController {
    CategoryService categoryService;
    @Autowired
    public CategoryController(CategoryService categoryService){
        this.categoryService=categoryService;
    }

    @PostMapping("/save")
    public String saveCategory(@RequestBody CategoryRequestDTO categoryRequestDTO){
        return categoryService.addCategory(categoryRequestDTO);
    }

    @GetMapping("/findById/{id}")
    public Category findCategoryById(@PathVariable int id){
        return categoryService.findCategoryById(id);
    }

    @GetMapping("/findAll")
    public List<Category> findAllCategory(){
        return categoryService.findAllCategory();
    }

    @PutMapping("/update/{id}")
    public String updateCategory(@PathVariable int id, @RequestBody CategoryRequestDTO categoryRequestDTO){
        return categoryService.updateCategory(id,categoryRequestDTO);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCategory(@PathVariable int id){
        return categoryService.deleteCategory(id);
    }
}
