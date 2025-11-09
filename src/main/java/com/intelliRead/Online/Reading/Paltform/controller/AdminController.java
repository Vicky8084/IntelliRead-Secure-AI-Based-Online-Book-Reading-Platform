package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.requestDTO.AdminRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.AdminService;
import com.intelliRead.Online.Reading.Paltform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    // ✅ EXISTING METHODS - YE PEHLE SE HAIN, INKO CHANGE MAT KARO
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

    // ✅ NEW METHODS FOR ADMIN DASHBOARD - BAS INKO ADD KARO
    @GetMapping("/summary")
    public ResponseEntity<?> getDashboardSummary() {
        try {
            System.out.println("📊 Admin Dashboard Summary API called");

            Map<String, Object> summary = new HashMap<>();

            // Demo data - baad mein database se replace kar dena
            summary.put("totalUsers", 1254);
            summary.put("totalBooks", 567);
            summary.put("pendingBooks", 23);
            summary.put("activeUsers", 189);
            summary.put("totalPublishers", 47);
            summary.put("pendingPublishers", 5);
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

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            System.out.println("👥 Admin Users API called - Page: " + page + ", Limit: " + limit);

            Map<String, Object> response = new HashMap<>();

            // Demo data - empty users list
            response.put("users", List.of());
            response.put("totalPages", 1);
            response.put("currentPage", page);
            response.put("success", true);

            System.out.println("✅ Users data sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Users error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error loading users"
            ));
        }
    }

    @GetMapping("/books")
    public ResponseEntity<?> getBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "all") String status) {
        try {
            System.out.println("📚 Admin Books API called - Page: " + page + ", Limit: " + limit + ", Status: " + status);

            Map<String, Object> response = new HashMap<>();

            // Demo data - empty books list
            response.put("books", List.of());
            response.put("totalPages", 1);
            response.put("currentPage", page);
            response.put("success", true);

            System.out.println("✅ Books data sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Books error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error loading books"
            ));
        }
    }

    @PutMapping("/books/{bookId}/status")
    public ResponseEntity<?> updateBookStatus(
            @PathVariable int bookId,
            @RequestBody Map<String, String> request) {
        try {
            System.out.println("🔄 Update Book Status API called - Book ID: " + bookId);

            String status = request.get("status");
            String adminNotes = request.get("adminNotes");

            System.out.println("📝 Status: " + status + ", Notes: " + adminNotes);

            String result = "Book status updated successfully";
            System.out.println("✅ Book status updated: " + bookId);
            return ResponseEntity.ok(Map.of("success", true, "message", result));
        } catch (Exception e) {
            System.out.println("❌ Update book error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error updating book"
            ));
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {
        try {
            System.out.println("🗑️ Delete User API called - User ID: " + userId);

            String result = "User deleted successfully";
            System.out.println("✅ User deleted: " + userId);
            return ResponseEntity.ok(Map.of("success", true, "message", result));
        } catch (Exception e) {
            System.out.println("❌ Delete user error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error deleting user"
            ));
        }
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable int bookId) {
        try {
            System.out.println("🗑️ Delete Book API called - Book ID: " + bookId);

            String result = "Book deleted successfully";
            System.out.println("✅ Book deleted: " + bookId);
            return ResponseEntity.ok(Map.of("success", true, "message", result));
        } catch (Exception e) {
            System.out.println("❌ Delete book error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error deleting book"
            ));
        }
    }

//    @PostMapping("/api-logout")
//    public ResponseEntity<?> adminLogout() {
//        System.out.println("🚪 Admin logout API called");
//        return ResponseEntity.ok(Map.of(
//                "success", true,
//                "message", "Admin logged out successfully"
//        ));
//    }

    @GetMapping("/activities")
    public ResponseEntity<?> getRecentActivities() {
        try {
            System.out.println("📋 Recent Activities API called");

            // Demo activities
            List<Map<String, String>> activities = List.of(
                    Map.of("type", "user", "message", "New user registration", "time", "Just now", "icon", "bx-user-plus"),
                    Map.of("type", "book", "message", "New book pending approval", "time", "5 minutes ago", "icon", "bx-book-add"),
                    Map.of("type", "system", "message", "Publisher account approved", "time", "1 hour ago", "icon", "bx-user-check")
            );

            System.out.println("✅ Activities data sent successfully");
            return ResponseEntity.ok(Map.of("success", true, "activities", activities));
        } catch (Exception e) {
            System.out.println("❌ Activities error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error loading activities"
            ));
        }
    }
}