package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.model.DownloadHistory;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.BookRepository;
import com.intelliRead.Online.Reading.Paltform.repository.DownloadHistoryRepository;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.DownloadRequestDto;
import com.intelliRead.Online.Reading.Paltform.responseDTO.DownloadResponseDto;
import com.intelliRead.Online.Reading.Paltform.util.FileStorageUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DownloadService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DownloadHistoryRepository downloadHistoryRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public DownloadResponseDto prepareDownload(DownloadRequestDto downloadRequest, HttpServletRequest request) {
        try {
            // Validate user exists
            Optional<User> userOptional = userRepository.findById(downloadRequest.getUserId());
            if (userOptional.isEmpty()) {
                return new DownloadResponseDto(false, "User not found", null, null, null, null, 0);
            }

            // Validate book exists
            Optional<Book> bookOptional = bookRepository.findById(downloadRequest.getBookId());
            if (bookOptional.isEmpty()) {
                return new DownloadResponseDto(false, "Book not found", null, null, null, null, 0);
            }

            User user = userOptional.get();
            Book book = bookOptional.get();

            // Validate book has file
            if (book.getFilePath() == null || book.getFilePath().isEmpty()) {
                return new DownloadResponseDto(false, "Book file not available for download", null, null, null, null, 0);
            }

            // Check if file exists physically
            Path filePath = Paths.get(uploadDir).resolve(book.getFilePath()).normalize();
            if (!Files.exists(filePath)) {
                return new DownloadResponseDto(false, "Book file not found on server", null, null, null, null, 0);
            }

            // Record download history
            DownloadHistory downloadHistory = new DownloadHistory();
            downloadHistory.setUser(user);
            downloadHistory.setBook(book);
            downloadHistory.setDownloadType(downloadRequest.getDownloadType());
            downloadHistory.setDownloadedAt(LocalDateTime.now());
            downloadHistory.setIpAddress(getClientIpAddress(request));
            downloadHistory.setUserAgent(request.getHeader("User-Agent"));

            downloadHistoryRepository.save(downloadHistory);

            // Get download count for this book
            int downloadCount = downloadHistoryRepository.countByBookId(book.getId());

            // Prepare response
            DownloadResponseDto response = new DownloadResponseDto();
            response.setSuccess(true);
            response.setMessage("Download ready");
            response.setFileName(book.getFileName());
            response.setFileType(book.getFileType());
            response.setFileSize(book.getFileSize());
            response.setDownloadCount(downloadCount);

            return response;

        } catch (Exception e) {
            return new DownloadResponseDto(false, "Download preparation failed: " + e.getMessage(),
                    null, null, null, null, 0);
        }
    }

    public ResponseEntity<Resource> downloadBookFile(int bookId, HttpServletRequest request) {
        try {
            Optional<Book> bookOptional = bookRepository.findById(bookId);
            if (bookOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Book book = bookOptional.get();

            if (book.getFilePath() == null || book.getFilePath().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Resolve file path
            Path filePath = Paths.get(uploadDir).resolve(book.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Determine content type
            String contentType = FileStorageUtil.getMimeType(book.getFileName());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + book.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public int getUserDownloadCount(int userId) {
        return downloadHistoryRepository.countByUserId(userId);
    }

    public int getBookDownloadCount(int bookId) {
        return downloadHistoryRepository.countByBookId(bookId);
    }

    public boolean hasUserDownloadedBook(int userId, int bookId) {
        return downloadHistoryRepository.countDownloadsByUserAndBook(userId, bookId) > 0;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}