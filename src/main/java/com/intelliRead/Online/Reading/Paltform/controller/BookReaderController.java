
package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reader")
public class BookReaderController {

    @Autowired
    private BookService bookService;

    @GetMapping("/{bookId}")
    public ResponseEntity<?> readBook(@PathVariable int bookId,
                                      @RequestParam(required = false) String mode) {
        try {
            Book book = bookService.findBookById(bookId);
            if (book == null) {
                return ResponseEntity.notFound().build();
            }

            if ("text".equals(mode) && book.getExtractedText() != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("type", "text");
                response.put("content", book.getExtractedText());
                response.put("book", book);

                return ResponseEntity.ok().body(response);
            } else {
                if (book.getFilePath() == null) {
                    return ResponseEntity.notFound().build();
                }

                Path filePath = Paths.get("uploads").resolve(book.getFilePath()).normalize();
                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists()) {
                    String contentType = "pdf".equalsIgnoreCase(book.getFileType())
                            ? "application/pdf" : "text/plain";

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "inline; filename=\"" + book.getFileName() + "\"")
                            .body(resource);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
