package com.intelliRead.Online.Reading.Paltform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.requestDTO.BookRequestDTO;
import com.intelliRead.Online.Reading.Paltform.responseDTO.BookWithCategoryDTO;
import com.intelliRead.Online.Reading.Paltform.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/book/apies")
public class BookController {

    BookService bookService;

    @Autowired
    public BookController(BookService bookService){
        this.bookService = bookService;
    }

    // ✅ EXISTING METHODS - YE PEHLE SE HAIN
    @PostMapping("/upload")
    public ResponseEntity<String> uploadBookWithFiles(
            @RequestParam("book") String bookRequestJson,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "cover", required = false) MultipartFile cover) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BookRequestDTO bookRequestDTO = objectMapper.readValue(bookRequestJson, BookRequestDTO.class);

            String response = bookService.addBookWithFiles(bookRequestDTO, file, cover);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findBookById(@PathVariable int id){
        try {
            Book book = bookService.findBookById(id);
            if (book != null) {
                return ResponseEntity.ok(book);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Book>> findAllBook(){
        try {
            List<Book> books = bookService.findAllBook();
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ KEEP: Get books by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Book>> findBooksByUserId(@PathVariable int userId){
        try {
            List<Book> books = bookService.findBooksByUserId(userId);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ KEEP: Get books by category ID
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Book>> findBooksByCategoryId(@PathVariable int categoryId){
        try {
            List<Book> books = bookService.findBooksByCategoryId(categoryId);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ KEEP: Get books by category name
    @GetMapping("/category/name/{categoryName}")
    public ResponseEntity<List<Book>> findBooksByCategoryName(@PathVariable String categoryName){
        try {
            List<Book> books = bookService.findBooksByCategoryName(categoryName);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("Update/{id}")
    public ResponseEntity<String> updateBook(@PathVariable int id, @RequestBody BookRequestDTO bookRequestDTO){
        try {
            String response = bookService.updateBook(id, bookRequestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable int id){
        try {
            String response = bookService.deleteBook(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/user-with-categories/{userId}")
    public ResponseEntity<List<BookWithCategoryDTO>> findBooksByUserIdWithCategories(@PathVariable int userId) {
        try {
            List<Book> books = bookService.findBooksByUserId(userId);
            List<BookWithCategoryDTO> bookDTOs = books.stream().map(book -> {
                BookWithCategoryDTO dto = new BookWithCategoryDTO();
                dto.setId(book.getId());
                dto.setTitle(book.getTitle());
                dto.setAuthor(book.getAuthor());
                dto.setDescription(book.getDescription());
                dto.setLanguage(book.getLanguage());
                dto.setStatus(book.getStatus().name());

                if (book.getCategory() != null) {
                    dto.setCategoryName(book.getCategory().getCategoryName());
                    dto.setCategoryId(book.getCategory().getId());
                }

                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(bookDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ NEW: Get ALL PUBLISHED books for books page
    @GetMapping("/published")
    public ResponseEntity<List<Book>> getAllPublishedBooks() {
        try {
            List<Book> allBooks = bookService.findAllBook();

            // Filter only published/approved books
            List<Book> publishedBooks = allBooks.stream()
                    .filter(book ->
                            book.getStatus() != null &&
                                    (book.getStatus().name().equals("PUBLISHED") ||
                                            book.getStatus().name().equals("APPROVED") ||
                                            book.getStatus().name().equals("ACTIVE"))
                    )
                    .collect(Collectors.toList());

            return ResponseEntity.ok(publishedBooks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ NEW: Get books by category for books page
    @GetMapping("/published/category/{categoryId}")
    public ResponseEntity<List<Book>> getPublishedBooksByCategory(@PathVariable int categoryId) {
        try {
            List<Book> categoryBooks = bookService.findBooksByCategoryId(categoryId);

            // Filter only published books
            List<Book> publishedBooks = categoryBooks.stream()
                    .filter(book ->
                            book.getStatus() != null &&
                                    (book.getStatus().name().equals("PUBLISHED") ||
                                            book.getStatus().name().equals("APPROVED") ||
                                            book.getStatus().name().equals("ACTIVE"))
                    )
                    .collect(Collectors.toList());

            return ResponseEntity.ok(publishedBooks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ NEW: Search published books
    @GetMapping("/published/search")
    public ResponseEntity<List<Book>> searchPublishedBooks(@RequestParam String query) {
        try {
            List<Book> allBooks = bookService.findAllBook();

            List<Book> searchResults = allBooks.stream()
                    .filter(book ->
                            (book.getStatus() != null &&
                                    (book.getStatus().name().equals("PUBLISHED") ||
                                            book.getStatus().name().equals("APPROVED") ||
                                            book.getStatus().name().equals("ACTIVE"))) &&
                                    (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                            book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                                            (book.getCategory() != null &&
                                                    book.getCategory().getCategoryName().toLowerCase().contains(query.toLowerCase())))
                    )
                    .collect(Collectors.toList());

            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}