package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.requestDTO.AdminRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.AdminService;
import com.intelliRead.Online.Reading.Paltform.service.BookService;
import com.intelliRead.Online.Reading.Paltform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private BookService bookService;

    // ✅ EXISTING METHODS - UNCHANGED
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerAdmin(@RequestBody AdminRequestDTO dto) {
        return ResponseEntity.badRequest().body(Map.of(
                "message", "❌ Admin registration disabled. Admins can only be created during application startup.",
                "success", "false"
        ));
    }

    @GetMapping("/approve/{userId}")
    public ResponseEntity<Map<String, String>> approveAdmin(@PathVariable int userId) {
        try {
            String result = userService.approveAdmin(userId);
            if (result.startsWith("✅")) {
                return ResponseEntity.ok(Map.of("message", result, "success", "true"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", result, "success", "false"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "success", "false"));
        }
    }

    @GetMapping("/reject/{userId}")
    public ResponseEntity<Map<String, String>> rejectAdmin(@PathVariable int userId) {
        try {
            String result = userService.rejectAdmin(userId);
            if (result.startsWith("✅")) {
                return ResponseEntity.ok(Map.of("message", result, "success", "true"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", result, "success", "false"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "success", "false"));
        }
    }

    // ✅ DASHBOARD SUMMARY - WORKING
    @GetMapping("/summary")
    public ResponseEntity<?> getDashboardSummary() {
        try {
            System.out.println("📊 Admin Dashboard Summary API called");

            List<User> allUsers = userService.getAllUser();
            List<Book> allBooks = bookService.findAllBook();

            long totalUsers = allUsers.size();
            long totalBooks = allBooks.size();
            long pendingBooks = allBooks.stream()
                    .filter(book -> book.getStatus() != null &&
                            book.getStatus().name().equals("PENDING"))
                    .count();
            long activeUsers = allUsers.stream()
                    .filter(user -> user.getStatus() != null &&
                            user.getStatus().name().equals("ACTIVE"))
                    .count();

            long totalPublishers = allUsers.stream()
                    .filter(user -> user.getRole() != null &&
                            user.getRole().name().equals("PUBLISHER"))
                    .count();
            long pendingPublishers = allUsers.stream()
                    .filter(user -> user.getRole() != null &&
                            user.getRole().name().equals("PUBLISHER") &&
                            user.getStatus() != null &&
                            user.getStatus().name().equals("PENDING"))
                    .count();

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalUsers", totalUsers);
            summary.put("totalBooks", totalBooks);
            summary.put("pendingBooks", pendingBooks);
            summary.put("activeUsers", activeUsers);
            summary.put("totalPublishers", totalPublishers);
            summary.put("pendingPublishers", pendingPublishers);
            summary.put("success", true);

            System.out.println("✅ Dashboard summary sent successfully");
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            System.out.println("❌ Dashboard error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error loading dashboard"
            ));
        }
    }

    // ✅ GET USERS - WORKING
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            System.out.println("👥 Admin Users API called - Page: " + page + ", Limit: " + limit);

            List<User> allUsers = userService.getAllUser();

            int startIndex = (page - 1) * limit;
            int endIndex = Math.min(startIndex + limit, allUsers.size());

            List<Map<String, Object>> safeUsers = allUsers.stream()
                    .skip(startIndex)
                    .limit(limit)
                    .map(this::convertUserToSafeMap)
                    .collect(Collectors.toList());

            int totalPages = (int) Math.ceil((double) allUsers.size() / limit);

            Map<String, Object> response = new HashMap<>();
            response.put("users", safeUsers);
            response.put("totalPages", totalPages);
            response.put("currentPage", page);
            response.put("totalUsers", allUsers.size());
            response.put("success", true);

            System.out.println("✅ Users data sent successfully - " + safeUsers.size() + " users");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Users error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error loading users"
            ));
        }
    }

    // ✅ GET BOOKS - WORKING
    @GetMapping("/books")
    public ResponseEntity<?> getBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "all") String status) {
        try {
            System.out.println("📚 Admin Books API called - Page: " + page + ", Limit: " + limit + ", Status: " + status);

            List<Book> allBooks = bookService.findAllBook();

            if (!"all".equals(status)) {
                allBooks = allBooks.stream()
                        .filter(book -> book.getStatus() != null &&
                                book.getStatus().name().equalsIgnoreCase(status))
                        .collect(Collectors.toList());
            }

            int startIndex = (page - 1) * limit;
            int endIndex = Math.min(startIndex + limit, allBooks.size());

            List<Map<String, Object>> safeBooks = allBooks.stream()
                    .skip(startIndex)
                    .limit(limit)
                    .map(this::convertBookToSafeMap)
                    .collect(Collectors.toList());

            int totalPages = (int) Math.ceil((double) allBooks.size() / limit);

            Map<String, Object> response = new HashMap<>();
            response.put("books", safeBooks);
            response.put("totalPages", totalPages);
            response.put("currentPage", page);
            response.put("totalBooks", allBooks.size());
            response.put("success", true);

            System.out.println("✅ Books data sent successfully - " + safeBooks.size() + " books");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Books error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error loading books"
            ));
        }
    }

    // ✅ APPROVE BOOK - NEW WORKING METHOD
    @PutMapping("/books/{bookId}/approve")
    public ResponseEntity<?> approveBook(@PathVariable int bookId) {
        try {
            System.out.println("✅ Approve Book API called - Book ID: " + bookId);

            String result = bookService.updateBookStatus(bookId, "APPROVED", "Book approved by admin");

            if (result.startsWith("✅")) {
                System.out.println("✅ Book approved: " + bookId);
                return ResponseEntity.ok(Map.of("success", true, "message", result));
            } else {
                System.out.println("❌ Book approval failed: " + result);
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", result));
            }
        } catch (Exception e) {
            System.out.println("❌ Approve book error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error approving book: " + e.getMessage()
            ));
        }
    }

    // ✅ REJECT BOOK - NEW WORKING METHOD
    @PutMapping("/books/{bookId}/reject")
    public ResponseEntity<?> rejectBook(@PathVariable int bookId, @RequestBody Map<String, String> request) {
        try {
            System.out.println("❌ Reject Book API called - Book ID: " + bookId);

            String reason = request.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Rejection reason is required"
                ));
            }

            String result = bookService.updateBookStatus(bookId, "REJECTED", "Rejected: " + reason);

            if (result.startsWith("✅")) {
                System.out.println("✅ Book rejected: " + bookId);
                return ResponseEntity.ok(Map.of("success", true, "message", result));
            } else {
                System.out.println("❌ Book rejection failed: " + result);
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", result));
            }
        } catch (Exception e) {
            System.out.println("❌ Reject book error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error rejecting book: " + e.getMessage()
            ));
        }
    }

    // ✅ UPDATE BOOK STATUS - WORKING
    @PutMapping("/books/{bookId}/status")
    public ResponseEntity<?> updateBookStatus(
            @PathVariable int bookId,
            @RequestBody Map<String, String> request) {
        try {
            System.out.println("🔄 Update Book Status API called - Book ID: " + bookId);

            String status = request.get("status");
            String adminNotes = request.get("adminNotes");

            System.out.println("📝 Status: " + status + ", Notes: " + adminNotes);

            String result = bookService.updateBookStatus(bookId, status, adminNotes);

            if (result.startsWith("✅")) {
                System.out.println("✅ Book status updated: " + bookId + " to " + status);
                return ResponseEntity.ok(Map.of("success", true, "message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", result));
            }
        } catch (Exception e) {
            System.out.println("❌ Update book error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error updating book: " + e.getMessage()
            ));
        }
    }

    // ✅ DELETE BOOK - WORKING
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable int bookId) {
        try {
            System.out.println("🗑️ Delete Book API called - Book ID: " + bookId);

            String result = bookService.deleteBook(bookId);

            if (result.startsWith("✅")) {
                System.out.println("✅ Book deleted: " + bookId);
                return ResponseEntity.ok(Map.of("success", true, "message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", result));
            }
        } catch (Exception e) {
            System.out.println("❌ Delete book error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error deleting book: " + e.getMessage()
            ));
        }
    }

    // ✅ APPROVE PUBLISHER - WORKING
    @PutMapping("/publishers/{userId}/approve")
    public ResponseEntity<?> approvePublisher(@PathVariable int userId) {
        try {
            System.out.println("✅ Approve Publisher API called - User ID: " + userId);

            String result = userService.approvePublisher(userId);

            if (result.startsWith("✅")) {
                System.out.println("✅ Publisher approved: " + userId);
                return ResponseEntity.ok(Map.of("success", true, "message", result));
            } else {
                System.out.println("❌ Publisher approval failed: " + result);
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", result));
            }
        } catch (Exception e) {
            System.out.println("❌ Approve publisher error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error approving publisher: " + e.getMessage()
            ));
        }
    }

    // ✅ REJECT PUBLISHER - WORKING
    @PutMapping("/publishers/{userId}/reject")
    public ResponseEntity<?> rejectPublisher(@PathVariable int userId) {
        try {
            System.out.println("❌ Reject Publisher API called - User ID: " + userId);

            String result = userService.rejectPublisher(userId);

            if (result.startsWith("✅")) {
                System.out.println("✅ Publisher rejected: " + userId);
                return ResponseEntity.ok(Map.of("success", true, "message", result));
            } else {
                System.out.println("❌ Publisher rejection failed: " + result);
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", result));
            }
        } catch (Exception e) {
            System.out.println("❌ Reject publisher error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error rejecting publisher: " + e.getMessage()
            ));
        }
    }

    // ✅ DELETE USER - WORKING
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {
        try {
            System.out.println("🗑️ Delete User API called - User ID: " + userId);

            String result = userService.deleteUserById(userId);

            if (result.startsWith("✅")) {
                System.out.println("✅ User deleted: " + userId);
                return ResponseEntity.ok(Map.of("success", true, "message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", result));
            }
        } catch (Exception e) {
            System.out.println("❌ Delete user error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error deleting user: " + e.getMessage()
            ));
        }
    }

    // ✅ FEATURE BOOK - NEW WORKING METHOD
    @PutMapping("/books/{bookId}/feature")
    public ResponseEntity<?> featureBook(@PathVariable int bookId) {
        try {
            System.out.println("⭐ Feature Book API called - Book ID: " + bookId);

            String result = bookService.featureBook(bookId);

            if (result.startsWith("✅")) {
                System.out.println("✅ Book featured: " + bookId);
                return ResponseEntity.ok(Map.of("success", true, "message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", result));
            }
        } catch (Exception e) {
            System.out.println("❌ Feature book error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error featuring book: " + e.getMessage()
            ));
        }
    }

    // ✅ UNFEATURE BOOK - NEW WORKING METHOD
    @PutMapping("/books/{bookId}/unfeature")
    public ResponseEntity<?> unfeatureBook(@PathVariable int bookId) {
        try {
            System.out.println("🔻 Unfeature Book API called - Book ID: " + bookId);

            String result = bookService.unfeatureBook(bookId);

            if (result.startsWith("✅")) {
                System.out.println("✅ Book unfeatured: " + bookId);
                return ResponseEntity.ok(Map.of("success", true, "message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", result));
            }
        } catch (Exception e) {
            System.out.println("❌ Unfeature book error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error unfeaturing book: " + e.getMessage()
            ));
        }
    }

    // ✅ HELPER METHODS - SAME AS BEFORE
    private Map<String, Object> convertUserToSafeMap(User user) {
        Map<String, Object> safeUser = new HashMap<>();
        safeUser.put("_id", user.getId());
        safeUser.put("fullName", user.getName());
        safeUser.put("name", user.getName());
        safeUser.put("email", user.getEmail());
        safeUser.put("role", user.getRole() != null ? user.getRole().name() : "USER");
        safeUser.put("isApproved", user.getStatus() != null && user.getStatus().name().equals("ACTIVE"));
        safeUser.put("isActive", user.getStatus() != null && user.getStatus().name().equals("ACTIVE"));
        safeUser.put("createdAt", user.getCreatedDate());
        safeUser.put("preferredLanguage", user.getPreferredLanguage());
        return safeUser;
    }

    private Map<String, Object> convertBookToSafeMap(Book book) {
        Map<String, Object> safeBook = new HashMap<>();
        safeBook.put("_id", book.getId());
        safeBook.put("title", book.getTitle());
        safeBook.put("author", book.getAuthor());
        safeBook.put("description", book.getDescription());
        safeBook.put("language", book.getLanguage());
        safeBook.put("status", book.getStatus() != null ? book.getStatus().name().toLowerCase() : "pending");
        safeBook.put("createdAt", book.getUploadedAt());
        safeBook.put("coverImage", book.getCoverImagePath());
        safeBook.put("fileName", book.getFileName());
        safeBook.put("fileType", book.getFileType());

        if (book.getCategory() != null) {
            safeBook.put("category", book.getCategory().getCategoryName());
            safeBook.put("categoryId", book.getCategory().getId());
        } else {
            safeBook.put("category", "Uncategorized");
        }

        if (book.getUser() != null) {
            safeBook.put("publisherName", book.getUser().getName());
            safeBook.put("publisherEmail", book.getUser().getEmail());
            safeBook.put("publisherId", book.getUser().getId());
        } else {
            safeBook.put("publisherName", "Unknown Publisher");
        }

        return safeBook;
    }

    // ✅ REST OF THE METHODS...
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable int userId) {
        try {
            System.out.println("👤 Get User Details API called - User ID: " + userId);

            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> safeUser = convertUserToSafeMap(user);
            safeUser.put("booksPublished", user.getBookList() != null ? user.getBookList().size() : 0);
            safeUser.put("accountStatus", user.getStatus() != null ? user.getStatus().name() : "UNKNOWN");

            Map<String, Object> response = new HashMap<>();
            response.put("user", safeUser);
            response.put("success", true);

            System.out.println("✅ User details sent successfully: " + user.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ User details error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error loading user details"
            ));
        }
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<?> getBookDetails(@PathVariable int bookId) {
        try {
            System.out.println("📖 Get Book Details API called - Book ID: " + bookId);

            Book book = bookService.findBookById(bookId);
            if (book == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> safeBook = convertBookToSafeMap(book);
            safeBook.put("fileSize", book.getFileSize());
            safeBook.put("extractedText", book.getExtractedText());
            safeBook.put("uploadedAt", book.getUploadedAt());

            Map<String, Object> response = new HashMap<>();
            response.put("book", safeBook);
            response.put("success", true);

            System.out.println("✅ Book details sent successfully: " + book.getTitle());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Book details error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error loading book details"
            ));
        }
    }

    @GetMapping("/activities")
    public ResponseEntity<?> getRecentActivities() {
        try {
            System.out.println("📋 Recent Activities API called");

            List<Book> recentBooks = bookService.findAllBook().stream()
                    .limit(5)
                    .collect(Collectors.toList());

            List<User> recentUsers = userService.getAllUser().stream()
                    .limit(3)
                    .collect(Collectors.toList());

            List<Map<String, String>> activities = new java.util.ArrayList<>();

            for (Book book : recentBooks) {
                activities.add(Map.of(
                        "type", "book",
                        "message", "New book uploaded: " + book.getTitle(),
                        "time", formatTimeAgo(book.getUploadedAt()),
                        "icon", "bx-book-add"
                ));
            }

            for (User user : recentUsers) {
                activities.add(Map.of(
                        "type", "user",
                        "message", "New user registered: " + user.getName(),
                        "time", formatTimeAgo(user.getCreatedDate()),
                        "icon", "bx-user-plus"
                ));
            }

            activities.add(Map.of(
                    "type", "system",
                    "message", "System maintenance completed",
                    "time", "2 hours ago",
                    "icon", "bx-cog"
            ));

            System.out.println("✅ Activities data sent successfully - " + activities.size() + " activities");
            return ResponseEntity.ok(Map.of("success", true, "activities", activities));
        } catch (Exception e) {
            System.out.println("❌ Activities error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error loading activities"
            ));
        }
    }

    private String formatTimeAgo(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "Recently";

        java.time.Duration duration = java.time.Duration.between(dateTime, java.time.LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minutes ago";
        if (hours < 24) return hours + " hours ago";
        if (days < 7) return days + " days ago";
        return "Over a week ago";
    }
}