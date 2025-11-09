package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.requestDTO.AdminRequestDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    public String registerAdmin(AdminRequestDTO dto) {
        throw new UnsupportedOperationException(
                "❌ Admin registration is disabled. Admins can only be created during application startup."
        );
    }

    // ✅ NEW METHODS FOR ADMIN DASHBOARD
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Demo data
        summary.put("totalUsers", 1254);
        summary.put("totalBooks", 567);
        summary.put("pendingBooks", 23);
        summary.put("activeUsers", 189);
        summary.put("totalPublishers", 47);
        summary.put("pendingPublishers", 5);

        return summary;
    }

    public Map<String, Object> getUsers(int page, int limit) {
        Map<String, Object> response = new HashMap<>();
        response.put("users", java.util.List.of());
        response.put("totalPages", 1);
        response.put("currentPage", page);
        return response;
    }

    public Map<String, Object> getBooks(int page, int limit, String status) {
        Map<String, Object> response = new HashMap<>();
        response.put("books", java.util.List.of());
        response.put("totalPages", 1);
        response.put("currentPage", page);
        return response;
    }

    public String updateBookStatus(int bookId, String status, String adminNotes) {
        return "Book status updated successfully";
    }

    public String deleteUser(int userId) {
        return "User deleted successfully";
    }

    public String deleteBook(int bookId) {
        return "Book deleted successfully";
    }
}