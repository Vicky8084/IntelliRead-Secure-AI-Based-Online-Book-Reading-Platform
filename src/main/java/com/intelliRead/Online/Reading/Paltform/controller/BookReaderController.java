package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reader")
public class BookReaderController {

    @Autowired
    private BookService bookService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @GetMapping("/{bookId}")
    public ResponseEntity<?> readBook(@PathVariable int bookId,
                                      @RequestParam(required = false) String mode) {
        try {
            System.out.println("📖 Book Reader API called - Book ID: " + bookId);

            Book book = bookService.findBookById(bookId);
            if (book == null) {
                System.out.println("❌ Book not found with ID: " + bookId);
                return ResponseEntity.notFound().build();
            }

            System.out.println("📚 Book found: " + book.getTitle());
            System.out.println("📁 File path: " + book.getFilePath());

            // ✅ TEXT MODE - Return extracted text
            if ("text".equals(mode) && book.getExtractedText() != null) {
                System.out.println("📝 Returning text content");
                Map<String, Object> response = new HashMap<>();
                response.put("type", "text");
                response.put("content", book.getExtractedText());
                response.put("book", book);
                return ResponseEntity.ok().body(response);
            }

            // ✅ FILE MODE - Serve the actual file
            if (book.getFilePath() == null || book.getFilePath().isEmpty()) {
                System.out.println("❌ File path is null or empty");
                return ResponseEntity.notFound().build();
            }

            // ✅ CORRECTED: Build complete file path
            Path filePath = Paths.get(uploadDir).resolve(book.getFilePath()).normalize();
            System.out.println("🔍 Looking for file at: " + filePath.toAbsolutePath());

            if (!Files.exists(filePath)) {
                System.out.println("❌ File not found at: " + filePath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                System.out.println("❌ Resource does not exist");
                return ResponseEntity.notFound().build();
            }

            // ✅ Determine content type
            String contentType = determineContentType(book.getFileType());
            System.out.println("📋 Serving file with content type: " + contentType);

            // ✅ ADD CORS HEADERS EXPLICITLY
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + book.getFileName() + "\"")
                    .header("Access-Control-Allow-Origin", "*")  // ✅ CORS FIX
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Authorization, Content-Type")
                    .body(resource);

        } catch (Exception e) {
            System.err.println("❌ Error serving book file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to load book: " + e.getMessage()));
        }
    }

    private String determineContentType(String fileType) {
        if (fileType == null) {
            return "application/octet-stream";
        }

        switch (fileType.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "txt":
            case "text":
                return "text/plain";
            case "epub":
                return "application/epub+zip";
            default:
                return "application/octet-stream";
        }
    }

    // ✅ NEW: Get book info for reader initialization
    @GetMapping("/{bookId}/info")
    public ResponseEntity<?> getBookInfo(@PathVariable int bookId) {
        try {
            Book book = bookService.findBookById(bookId);
            if (book == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> bookInfo = new HashMap<>();
            bookInfo.put("id", book.getId());
            bookInfo.put("title", book.getTitle());
            bookInfo.put("author", book.getAuthor());
            bookInfo.put("description", book.getDescription());
            bookInfo.put("language", book.getLanguage());
            bookInfo.put("fileType", book.getFileType());
            bookInfo.put("fileName", book.getFileName());
            bookInfo.put("hasTextContent", book.getExtractedText() != null);

            return ResponseEntity.ok(bookInfo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to get book info"));
        }
    }
}