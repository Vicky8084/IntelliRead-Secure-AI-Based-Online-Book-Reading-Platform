package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.BookConverter;
import com.intelliRead.Online.Reading.Paltform.exception.BookAlreadyExistException;
import com.intelliRead.Online.Reading.Paltform.exception.UserNotFoundException;
import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.BookRepository;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.BookRequestDTO;
import com.intelliRead.Online.Reading.Paltform.util.FileStorageUtil;
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

    private final Path uploadsRoot;

    @Autowired
    public BookService(BookRepository bookRepository,
                       UserRepository userRepository,
                       @Value("${file.upload-dir:uploads}") String uploadDir) throws IOException {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
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
        bookRepository.save(book);
        return "Book saved Successfully";
    }

    /* --- Updated: Add book with uploaded file and optional cover using Book ID folders --- */
    public String addBookWithFiles(BookRequestDTO bookRequestDTO,
                                   MultipartFile file,
                                   MultipartFile cover) throws IOException {

        // Validate that book file is provided
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Book file is required");
        }

        // Validate book file type
        String originalFileName = file.getOriginalFilename();
        if (!FileStorageUtil.isValidBookFile(originalFileName)) {
            throw new IllegalArgumentException("Only PDF and TXT files are allowed for books");
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

        // Save book first to get auto-generated ID
        book = bookRepository.save(book);
        int bookId = book.getId();

        // handle main file
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

            System.out.println("File saved successfully at: " + targetPath);
        }

        // handle cover image
        if (cover != null && !cover.isEmpty()) {
            // Validate cover image type
            String coverFileName = cover.getOriginalFilename();
            if (!FileStorageUtil.isValidImageFile(coverFileName)) {
                throw new IllegalArgumentException("Only JPG, JPEG, PNG, and GIF images are allowed for covers");
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

    public Book findBookById(int id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        return bookOptional.orElse(null);
    }

    public List<Book> findAllBook() {
        return bookRepository.findAll();
    }

    // ✅ CORRECTED: Backward compatible version (without userId check for now)
    public String updateBook(int id, BookRequestDTO bookRequestDTO) {
        Book book = findBookById(id);
        if (book != null) {
            book.setTitle(bookRequestDTO.getTitle());
            book.setAuthor(bookRequestDTO.getAuthor());
            book.setDescription(bookRequestDTO.getDescription());
            book.setLanguage(bookRequestDTO.getLanguage());
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