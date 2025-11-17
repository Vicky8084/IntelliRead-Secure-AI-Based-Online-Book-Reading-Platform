package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.BookConverter;
import com.intelliRead.Online.Reading.Paltform.enums.BookStatus;
import com.intelliRead.Online.Reading.Paltform.exception.BookAlreadyExistException;
import com.intelliRead.Online.Reading.Paltform.exception.UserNotFoundException;
import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.model.Category;
import com.intelliRead.Online.Reading.Paltform.model.Review;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.BookRepository;
import com.intelliRead.Online.Reading.Paltform.repository.CategoryRepository;
import com.intelliRead.Online.Reading.Paltform.repository.ReviewRepository;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.BookRequestDTO;
import com.intelliRead.Online.Reading.Paltform.util.FileStorageUtil;
import jakarta.transaction.Transactional;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    BookRepository bookRepository;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    EmailService emailService;
    ReviewRepository reviewRepository;

    private final Path uploadsRoot;

    @Autowired
    public BookService(BookRepository bookRepository,
                       UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       EmailService emailService,
                       ReviewRepository reviewRepository,
                       @Value("${file.upload-dir:uploads}") String uploadDir) throws IOException {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.emailService = emailService;
        this.uploadsRoot = FileStorageUtil.ensureDirectory(uploadDir);
        this.reviewRepository=reviewRepository;
    }

    /* --- UPDATED: Complete file upload with text extraction --- */
    public String addBookWithFiles(BookRequestDTO bookRequestDTO,
                                   MultipartFile file,
                                   MultipartFile cover) throws IOException {

        // ✅ IMPROVED: Validate that book file is provided
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Book file is required");
        }

        String originalFileName = file.getOriginalFilename();

        // ✅ IMPROVED: Better file type validation with detailed error message
        if (!FileStorageUtil.isValidBookFile(originalFileName)) {
            throw new IllegalArgumentException(
                    "Only PDF and TXT files are allowed. Received: " +
                            FileStorageUtil.extension(originalFileName)
            );
        }

        // ✅ ADDED: File size validation
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        // ✅ CORRECTED: Use proper method name from repository
        Optional<Book> bookOptional = bookRepository.findByTitleAndUser_Id(
                bookRequestDTO.getTitle(), bookRequestDTO.getUserId());
        if (bookOptional.isPresent()) {
            throw new BookAlreadyExistException("You have already added a book with this title!");
        }

        User user = userRepository.findById(bookRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Book book = BookConverter.convertBookRequestDtoIntoBook(bookRequestDTO);
        book.setUser(user);
        book.setStatus(bookRequestDTO.getStatus());

        // ✅ ADDED: Handle category assignment
        if (bookRequestDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(bookRequestDTO.getCategoryId()).orElse(null);
            book.setCategory(category);
        }

        // Save book first to get auto-generated ID
        book = bookRepository.save(book);
        int bookId = book.getId();

        // ✅ FIXED: Handle main file with PROPER targetPath definition
        if (file != null && !file.isEmpty()) {
            String original = FileStorageUtil.sanitizeFileName(file.getOriginalFilename());
            String ext = FileStorageUtil.extension(original);
            String fileRelativeDir = "books/" + bookId;  // Use book ID as folder name
            Path targetDir = FileStorageUtil.ensureDirectory(uploadsRoot.resolve(fileRelativeDir).toString());

            // ✅ FIXED: Properly define targetPath
            Path targetPath = targetDir.resolve(original);

            System.out.println("📁 Saving file to: " + targetPath.toAbsolutePath());

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            book.setFileName(original);
            book.setFilePath(fileRelativeDir + "/" + original);  // Store relative path
            book.setFileType(ext);
            book.setFileSize(file.getSize());

            // ✅ DEBUG: Print file info
            System.out.println("✅ File saved successfully!");
            System.out.println("📁 Relative Path: " + book.getFilePath());
            System.out.println("📁 Absolute Path: " + targetPath.toAbsolutePath());
            System.out.println("📁 File exists: " + Files.exists(targetPath));
            System.out.println("📁 File size: " + file.getSize() + " bytes");

            // ✅ ADDED: TEXT EXTRACTION FOR PDF AND TXT FILES
            if ("pdf".equalsIgnoreCase(book.getFileType())) {
                try {
                    String extractedText = extractTextFromPdf(file);
                    book.setExtractedText(extractedText);
                    System.out.println("📝 PDF text extraction successful for book: " + book.getTitle());
                    System.out.println("📄 Extracted text length: " + (extractedText != null ? extractedText.length() : 0));
                } catch (Exception e) {
                    System.out.println("⚠️ PDF text extraction failed, but book saved: " + e.getMessage());
                    // Continue even if text extraction fails
                }
            }
            // ✅ ADDED: For TXT files
            else if ("txt".equalsIgnoreCase(book.getFileType())) {
                try {
                    String content = new String(file.getBytes());
                    book.setExtractedText(content);
                    System.out.println("📝 TXT content extraction successful for book: " + book.getTitle());
                } catch (Exception e) {
                    System.out.println("⚠️ TXT content reading failed: " + e.getMessage());
                    // Continue even if text reading fails
                }
            }
        }

        // ✅ FIXED: Handle cover image with PROPER coverPath definition
        if (cover != null && !cover.isEmpty()) {
            // Validate cover image type
            String coverFileName = cover.getOriginalFilename();
            if (!FileStorageUtil.isValidImageFile(coverFileName)) {
                throw new IllegalArgumentException("Only JPG, JPEG, PNG, and GIF images are allowed for covers");
            }

            // ✅ ADDED: Cover image size validation
            if (cover.getSize() > 5 * 1024 * 1024) { // 5MB for images
                throw new IllegalArgumentException("Cover image size exceeds 5MB limit");
            }

            String coverName = FileStorageUtil.sanitizeFileName(cover.getOriginalFilename());
            String coverExt = FileStorageUtil.extension(coverName);
            String coverRelativeDir = "covers/" + bookId;  // Use book ID as folder name
            Path coverDir = FileStorageUtil.ensureDirectory(uploadsRoot.resolve(coverRelativeDir).toString());

            // ✅ FIXED: Properly define coverPath
            Path coverPath = coverDir.resolve(coverName);

            try (InputStream in = cover.getInputStream()) {
                Files.copy(in, coverPath, StandardCopyOption.REPLACE_EXISTING);
            }

            book.setCoverImagePath(coverRelativeDir + "/" + coverName);
            System.out.println("🖼️ Cover image saved: " + coverPath.toAbsolutePath());
        }

        bookRepository.save(book);
        return "Book saved Successfully with files";
    }

    // ✅ ADDED: PDF Text Extraction Method
    private String extractTextFromPdf(MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            System.out.println("📄 PDF text extracted successfully");
            return text;
        } catch (Exception e) {
            System.out.println("❌ PDF text extraction failed: " + e.getMessage());
            return null;
        }
    }

    // ✅ EXISTING METHODS - UNCHANGED
    public Book findBookById(int id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        return bookOptional.orElse(null);
    }

    public List<Book> findAllBook() {
        return bookRepository.findAll();
    }

    public List<Book> findBooksByCategoryId(int categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    public List<Book> findBooksByCategoryName(String categoryName) {
        return bookRepository.findByCategoryCategoryName(categoryName);
    }

    public String updateBook(int id, BookRequestDTO bookRequestDTO) {
        Book book = findBookById(id);
        if (book != null) {
            book.setTitle(bookRequestDTO.getTitle());
            book.setAuthor(bookRequestDTO.getAuthor());
            book.setDescription(bookRequestDTO.getDescription());
            book.setLanguage(bookRequestDTO.getLanguage());
            book.setStatus(bookRequestDTO.getStatus());

            if (bookRequestDTO.getCategoryId() != null) {
                Category category = categoryRepository.findById(bookRequestDTO.getCategoryId()).orElse(null);
                book.setCategory(category);
            }

            bookRepository.save(book);
            return "Book Updated Successfully";
        } else {
            return "Book not found";
        }
    }

    @Transactional
    public String deleteBook(int id) {
        Book book = findBookById(id);
        if (book != null) {
            try {
                // ✅ STEP 1: First delete all reviews associated with this book
                deleteBookReviews(book.getId());

                // ✅ STEP 2: Delete physical files
                deleteBookFiles(book);

                // ✅ STEP 3: Finally delete the book (reviews will be cascade deleted)
                bookRepository.deleteById(id);

                return "Book and all associated reviews deleted successfully";

            } catch (Exception e) {
                throw new RuntimeException("Error deleting book: " + e.getMessage());
            }
        } else {
            return "Book Not Found";
        }
    }

    private void deleteBookReviews(int bookId) {
        try {
            List<Review> reviews = reviewRepository.findByBookId(bookId);
            if (!reviews.isEmpty()) {
                System.out.println("🗑️ Deleting " + reviews.size() + " reviews for book ID: " + bookId);
                reviewRepository.deleteAll(reviews);
                System.out.println("✅ Successfully deleted all reviews for book ID: " + bookId);
            } else {
                System.out.println("ℹ️ No reviews found for book ID: " + bookId);
            }
        } catch (Exception e) {
            System.err.println("❌ Error deleting reviews for book ID " + bookId + ": " + e.getMessage());
            throw e; // Re-throw to trigger transaction rollback
        }
    }


    // ✅ FIXED: Physical files delete karne ka method
    private void deleteBookFiles(Book book) {
        try {
            // Delete book file
            if (book.getFilePath() != null && !book.getFilePath().isEmpty()) {
                Path filePath = uploadsRoot.resolve(book.getFilePath());
                System.out.println("🗑️ Deleting book file: " + filePath.toAbsolutePath());
                Files.deleteIfExists(filePath);

                // Delete book directory if empty
                Path bookDir = filePath.getParent();
                if (Files.exists(bookDir) && Files.isDirectory(bookDir)) {
                    try (var stream = Files.list(bookDir)) {
                        if (stream.findFirst().isEmpty()) {
                            Files.deleteIfExists(bookDir);
                            System.out.println("🗑️ Deleted empty book directory: " + bookDir.toAbsolutePath());
                        }
                    }
                }
            }

            // Delete cover image
            if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
                Path coverPath = uploadsRoot.resolve(book.getCoverImagePath());
                System.out.println("🗑️ Deleting cover image: " + coverPath.toAbsolutePath());
                Files.deleteIfExists(coverPath);

                // Delete cover directory if empty
                Path coverDir = coverPath.getParent();
                if (Files.exists(coverDir) && Files.isDirectory(coverDir)) {
                    try (var stream = Files.list(coverDir)) {
                        if (stream.findFirst().isEmpty()) {
                            Files.deleteIfExists(coverDir);
                            System.out.println("🗑️ Deleted empty cover directory: " + coverDir.toAbsolutePath());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ File deletion failed: " + e.getMessage());
            // Continue with DB deletion even if file deletion fails
        }
    }

    public List<Book> findBooksByUserId(int userId) {
        return bookRepository.findBooksByUserIdWithCategory(userId);
    }

    public List<Book> findPublishedBooks() {
        List<Book> allBooks = bookRepository.findAll();
        return allBooks.stream()
                .filter(book ->
                        book.getStatus() != null &&
                                (book.getStatus().equals(BookStatus.PUBLISHED) ||
                                        book.getStatus().equals(BookStatus.APPROVED) ||
                                        book.getStatus().equals(BookStatus.ACTIVE))
                )
                .collect(Collectors.toList());
    }

    public List<Book> findPublishedBooksByCategoryId(int categoryId) {
        List<Book> categoryBooks = bookRepository.findByCategoryId(categoryId);
        return categoryBooks.stream()
                .filter(book ->
                        book.getStatus() != null &&
                                (book.getStatus().equals(BookStatus.PUBLISHED) ||
                                        book.getStatus().equals(BookStatus.APPROVED) ||
                                        book.getStatus().equals(BookStatus.ACTIVE))
                )
                .collect(Collectors.toList());
    }

    public List<Book> searchPublishedBooks(String query) {
        List<Book> allBooks = bookRepository.findAll();
        return allBooks.stream()
                .filter(book ->
                        (book.getStatus() != null &&
                                (book.getStatus().equals(BookStatus.PUBLISHED) ||
                                        book.getStatus().equals(BookStatus.APPROVED) ||
                                        book.getStatus().equals(BookStatus.ACTIVE))) &&
                                (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                        book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                                        (book.getCategory() != null &&
                                                book.getCategory().getCategoryName().toLowerCase().contains(query.toLowerCase())))
                )
                .collect(Collectors.toList());
    }

    public String updateBookStatus(int bookId, String status, String adminNotes) {
        try {
            Book book = findBookById(bookId);
            if (book == null) {
                return "❌ Book not found";
            }

            BookStatus bookStatus;
            try {
                bookStatus = BookStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return "❌ Invalid status: " + status;
            }

            book.setStatus(bookStatus);
            bookRepository.save(book);

            return "✅ Book status updated to " + status;
        } catch (Exception e) {
            return "❌ Error updating book status: " + e.getMessage();
        }
    }

    public String featureBook(int bookId) {
        try {
            Book book = findBookById(bookId);
            if (book == null) {
                return "❌ Book not found";
            }

            bookRepository.save(book);
            return "✅ Book featured successfully";
        } catch (Exception e) {
            return "❌ Error featuring book: " + e.getMessage();
        }
    }

    public String unfeatureBook(int bookId) {
        try {
            Book book = findBookById(bookId);
            if (book == null) {
                return "❌ Book not found";
            }

            bookRepository.save(book);
            return "✅ Book unfeatured successfully";
        } catch (Exception e) {
            return "❌ Error unfeaturing book: " + e.getMessage();
        }
    }

    public String rejectBook(int bookId, String rejectionReason) {
        try {
            Book book = findBookById(bookId);
            if (book == null) {
                return "❌ Book not found";
            }

            User publisher = book.getUser();
            if (publisher == null) {
                bookRepository.delete(book);
                return "✅ Book rejected and deleted (publisher not found)";
            }

            String bookTitle = book.getTitle();
            bookRepository.delete(book);

            try {
                emailService.sendBookRejectionEmail(publisher, book, rejectionReason);
                System.out.println("✅ Rejection email sent to publisher: " + publisher.getEmail());
            } catch (Exception emailError) {
                System.err.println("❌ Failed to send rejection email, but book was deleted: " + emailError.getMessage());
            }

            return "✅ Book '" + bookTitle + "' rejected and permanently deleted. Rejection email sent to publisher.";

        } catch (Exception e) {
            return "❌ Error rejecting book: " + e.getMessage();
        }
    }

    public int getBooksCountByUserId(int userId) {
        try {
            return bookRepository.countByUserId(userId);
        } catch (Exception e) {
            System.out.println("❌ Error counting books for user " + userId + ": " + e.getMessage());
            return 0;
        }
    }
}