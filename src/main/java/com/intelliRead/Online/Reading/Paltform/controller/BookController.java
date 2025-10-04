package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.requestDTO.BookRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book/apies")
public class BookController {
    BookService bookService;
    @Autowired
    public BookController(BookService bookService){
        this.bookService=bookService;
    }

    @PostMapping("/save")
    public String saveBook(@RequestBody BookRequestDTO bookRequestDTO){
        return bookService.addBook(bookRequestDTO);
    }

    @GetMapping("/findById/{id}")
    public Book findBookById(@PathVariable int id){
        return bookService.findBookById(id);
    }

    @GetMapping("/findAll")
    public List<Book> findAllBook(){
        return bookService.findAllBook();
    }

    @PutMapping("Update/{id}")
    public String updateBook(@PathVariable int id, @RequestBody BookRequestDTO bookRequestDTO){
        return bookService.updateBook(id,bookRequestDTO);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable int id){
        return bookService.deleteBook(id);
    }
}
