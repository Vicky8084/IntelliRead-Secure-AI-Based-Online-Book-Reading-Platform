package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.BookConverter;
import com.intelliRead.Online.Reading.Paltform.exception.BookAlreadyExistException;
import com.intelliRead.Online.Reading.Paltform.exception.UserNotFoundException;
import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.model.Category;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.BookRepository;
import com.intelliRead.Online.Reading.Paltform.repository.CategoryRepository;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.BookRequestDTO;
import com.intelliRead.Online.Reading.Paltform.util.FileStorageUtil;
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
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    BookRepository bookRepository;
    UserRepository userRepository;
    CategoryRepository categoryRepository;

    private final Path uploadsRoot;

    @Autowired
    public BookService(BookRepository bookRepository,
                       UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       @Value("${file.upload-dir:uploads}") String uploadDir) throws IOException {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.uploadsRoot = FileStorageUtil.ensureDirectory(uploadDir);
    }

    /* Existing addBook that accepts only DTO -- kept for backwards compatibility */
    public String addBook(BookRequestDTO bookRequestDTO) {

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

        // ✅ ADDED: Handle category assignment
        if (bookRequestDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(bookRequestDTO.getCategoryId()).orElse(null);
            book.setCategory(category);
        }

        bookRepository.save(book);
        return "Book saved Successfully";
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

        // ✅ ADDED: Handle category assignment
        if (bookRequestDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(bookRequestDTO.getCategoryId()).orElse(null);
            book.setCategory(category);
        }

        // Save book first to get auto-generated ID
        book = bookRepository.save(book);
        int bookId = book.getId();

        // Handle main file
        if (file != null && !file.isEmpty()) {
            String original = FileStorageUtil.sanitizeFileName(file.getOriginalFilename());
            String ext = FileStorageUtil.extension(original);
            String fileRelativeDir = "books/" + bookId;  // Use book ID as folder name
            Path targetDir = FileStorageUtil.ensureDirectory(uploadsRoot.resolve(fileRelativeDir).toString());
            Path targetPath = targetDir.resolve(original);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            book.setFileName(original);
            book.setFilePath(fileRelativeDir + "/" + original);  // Store relative path
            book.setFileType(ext);
            book.setFileSize(file.getSize());

            // ✅ ADDED: TEXT EXTRACTION FOR PDF AND TXT FILES
            if ("pdf".equalsIgnoreCase(book.getFileType())) {
                try {
                    String extractedText = extractTextFromPdf(file);
                    book.setExtractedText(extractedText);
                    System.out.println("PDF text extraction successful for book: " + book.getTitle());
                } catch (Exception e) {
                    System.out.println("PDF text extraction failed, but book saved: " + e.getMessage());
                    // Continue even if text extraction fails
                }
            }
            // ✅ ADDED: For TXT files
            else if ("txt".equalsIgnoreCase(book.getFileType())) {
                try {
                    String content = new String(file.getBytes());
                    book.setExtractedText(content);
                    System.out.println("TXT content extraction successful for book: " + book.getTitle());
                } catch (Exception e) {
                    System.out.println("TXT content reading failed: " + e.getMessage());
                    // Continue even if text reading fails
                }
            }

            System.out.println("File saved successfully at: " + targetPath);
        }

        // Handle cover image
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
            Path coverPath = coverDir.resolve(coverName);

            try (InputStream in = cover.getInputStream()) {
                Files.copy(in, coverPath, StandardCopyOption.REPLACE_EXISTING);
            }
            book.setCoverImagePath(coverRelativeDir + "/" + coverName);
        }

        bookRepository.save(book);
        return "Book saved Successfully with files";
    }

    // ✅ ADDED: PDF Text Extraction Method
    private String extractTextFromPdf(MultipartFile file) {
        try {
            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            document.close();
            return text;
        } catch (Exception e) {
            System.out.println("PDF text extraction failed: " + e.getMessage());
            return null;
        }
    }

    public Book findBookById(int id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        return bookOptional.orElse(null);
    }

    public List<Book> findAllBook() {
        return bookRepository.findAll();
    }

    // ✅ ADDED: Get books by category
    public List<Book> findBooksByCategoryId(int categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    // ✅ ADDED: Get books by category name
    public List<Book> findBooksByCategoryName(String categoryName) {
        return bookRepository.findByCategoryCategoryName(categoryName);
    }

    // ✅ CORRECTED: Backward compatible version (without userId check for now)
    public String updateBook(int id, BookRequestDTO bookRequestDTO) {
        Book book = findBookById(id);
        if (book != null) {
            book.setTitle(bookRequestDTO.getTitle());
            book.setAuthor(bookRequestDTO.getAuthor());
            book.setDescription(bookRequestDTO.getDescription());
            book.setLanguage(bookRequestDTO.getLanguage());

            // ✅ ADDED: Update category if provided
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

    // ✅ CORRECTED: Backward compatible version (without userId check for now)
    public String deleteBook(int id) {
        Book book = findBookById(id);
        if (book != null) {
            // ✅ File cleanup
            deleteBookFiles(book);

            bookRepository.deleteById(id);
            return "Book Deleted Successfully";
        } else {
            return "Book Not Found";
        }
    }

    // ✅ NEW: Physical files delete karne ka method
    private void deleteBookFiles(Book book) {
        try {
            // Delete book file
            if (book.getFilePath() != null && !book.getFilePath().isEmpty()) {
                Path filePath = uploadsRoot.resolve(book.getFilePath());
                Files.deleteIfExists(filePath);

                // Delete book directory if empty
                Path bookDir = filePath.getParent();
                if (Files.exists(bookDir) && Files.isDirectory(bookDir)) {
                    // Check if directory is empty before deleting
                    try (var stream = Files.list(bookDir)) {
                        if (stream.findFirst().isEmpty()) {
                            Files.deleteIfExists(bookDir);
                        }
                    }
                }
            }

            // Delete cover image
            if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
                Path coverPath = uploadsRoot.resolve(book.getCoverImagePath());
                Files.deleteIfExists(coverPath);

                // Delete cover directory if empty
                Path coverDir = coverPath.getParent();
                if (Files.exists(coverDir) && Files.isDirectory(coverDir)) {
                    // Check if directory is empty before deleting
                    try (var stream = Files.list(coverDir)) {
                        if (stream.findFirst().isEmpty()) {
                            Files.deleteIfExists(coverDir);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("File deletion failed: " + e.getMessage());
            // Continue with DB deletion even if file deletion fails
        }
    }

    // ✅ NEW: Get books by specific publisher
    public List<Book> findBooksByUserId(int userId) {
        return bookRepository.findByUser_Id(userId);
    }
}