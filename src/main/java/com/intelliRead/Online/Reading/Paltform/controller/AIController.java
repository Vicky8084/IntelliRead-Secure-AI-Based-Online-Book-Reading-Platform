package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.requestDTO.AIQuestionRequestDTO;
import com.intelliRead.Online.Reading.Paltform.requestDTO.AISummaryRequestDTO;
import com.intelliRead.Online.Reading.Paltform.responseDTO.AIQuestionResponseDTO;
import com.intelliRead.Online.Reading.Paltform.responseDTO.AISummaryResponseDTO;
import com.intelliRead.Online.Reading.Paltform.service.AIService;
import com.intelliRead.Online.Reading.Paltform.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private BookService bookService;

    @PostMapping("/summary")
    public ResponseEntity<?> generateSummary(@RequestBody AISummaryRequestDTO request) {
        try {
            AISummaryResponseDTO response = aiService.generateSummary(request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Failed to generate summary",
                                "message", response.getErrorMessage())
                );
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "AI service error", "message", e.getMessage())
            );
        }
    }

    @PostMapping("/question")
    public ResponseEntity<?> answerQuestion(@RequestBody AIQuestionRequestDTO request) {
        try {
            AIQuestionResponseDTO response = aiService.answerQuestion(request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Failed to answer question",
                                "message", response.getErrorMessage())
                );
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "AI service error", "message", e.getMessage())
            );
        }
    }

    @GetMapping("/summary/book/{bookId}")
    public ResponseEntity<?> getBookSummary(@PathVariable int bookId) {
        try {
            Book book = bookService.findBookById(bookId);
            if (book == null) {
                return ResponseEntity.notFound().build();
            }

            // Use extracted text if available, otherwise use description
            String content = book.getExtractedText() != null ?
                    book.getExtractedText() : book.getDescription();

            AISummaryRequestDTO summaryRequest = new AISummaryRequestDTO();
            summaryRequest.setContent(content);
            summaryRequest.setType("BOOK");
            summaryRequest.setMaxLength(200);
            summaryRequest.setLanguage(book.getLanguage());

            AISummaryResponseDTO response = aiService.generateSummary(summaryRequest);

            // Add book info to response
            Map<String, Object> fullResponse = new HashMap<>();
            fullResponse.put("summary", response);
            fullResponse.put("bookInfo", Map.of(
                    "title", book.getTitle(),
                    "author", book.getAuthor(),
                    "category", book.getCategory() != null ? book.getCategory().getCategoryName() : "Unknown"
            ));

            return ResponseEntity.ok(fullResponse);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Failed to generate book summary", "message", e.getMessage())
            );
        }
    }

    @GetMapping("/summary/book/{bookId}/chapter/{chapterNumber}")
    public ResponseEntity<?> getChapterSummary(
            @PathVariable int bookId,
            @PathVariable int chapterNumber,
            @RequestParam(required = false) String chapterContent) {

        try {
            Book book = bookService.findBookById(bookId);
            if (book == null) {
                return ResponseEntity.notFound().build();
            }

            // In real implementation, you'd fetch chapter content from database
            String content = chapterContent != null ? chapterContent :
                    "Chapter " + chapterNumber + " of " + book.getTitle() +
                            " continues exploring the main themes introduced earlier. " +
                            "This section delves deeper into specific aspects and provides " +
                            "more detailed examples and case studies.";

            AISummaryRequestDTO summaryRequest = new AISummaryRequestDTO();
            summaryRequest.setContent(content);
            summaryRequest.setType("CHAPTER");
            summaryRequest.setMaxLength(150);
            summaryRequest.setLanguage(book.getLanguage());

            AISummaryResponseDTO response = aiService.generateSummary(summaryRequest);

            Map<String, Object> fullResponse = new HashMap<>();
            fullResponse.put("summary", response);
            fullResponse.put("chapterInfo", Map.of(
                    "chapterNumber", chapterNumber,
                    "bookTitle", book.getTitle()
            ));

            return ResponseEntity.ok(fullResponse);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Failed to generate chapter summary", "message", e.getMessage())
            );
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            boolean aiAvailable = aiService.isAIAvailable();
            Map<String, Object> healthInfo = new HashMap<>();
            healthInfo.put("aiService", aiAvailable ? "AVAILABLE" : "UNAVAILABLE");
            healthInfo.put("message", aiAvailable ?
                    "AI features are enabled" : "AI features are disabled (using mock responses)");
            healthInfo.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(healthInfo);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("status", "ERROR", "message", e.getMessage())
            );
        }
    }

    @PostMapping("/question/book/{bookId}")
    public ResponseEntity<?> askBookQuestion(
            @PathVariable int bookId,
            @RequestBody AIQuestionRequestDTO request) {

        try {
            Book book = bookService.findBookById(bookId);
            if (book == null) {
                return ResponseEntity.notFound().build();
            }

            // Set book context for the question
            if (request.getContext() == null && book.getExtractedText() != null) {
                // Use first 1000 characters as context
                String context = book.getExtractedText().length() > 1000 ?
                        book.getExtractedText().substring(0, 1000) : book.getExtractedText();
                request.setContext(context);
            }

            request.setBookId(bookId);

            AIQuestionResponseDTO response = aiService.answerQuestion(request);

            Map<String, Object> fullResponse = new HashMap<>();
            fullResponse.put("answer", response);
            fullResponse.put("bookInfo", Map.of(
                    "title", book.getTitle(),
                    "author", book.getAuthor()
            ));

            return ResponseEntity.ok(fullResponse);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Failed to process question", "message", e.getMessage())
            );
        }
    }
}