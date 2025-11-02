//package com.intelliRead.Online.Reading.Paltform.controller;
//
//import com.intelliRead.Online.Reading.Paltform.model.User;
//import com.intelliRead.Online.Reading.Paltform.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/admin")
//public class AdminDashboardController {
//
//    @Autowired
//    private UserService userService;
//
//    // ✅ Dashboard Summary
//    @GetMapping("/summary")
//    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
//        try {
//            Map<String, Object> response = new HashMap<>();
//
//            // Get all users
//            List<User> allUsers = userService.getAllUser();
//            long totalUsers = allUsers.size();
//            long activeUsers = allUsers.stream().filter(user ->
//                    user.getStatus().name().equals("ACTIVE")).count();
//
//            // Count publishers (ADMIN role in your system)
//            long totalPublishers = allUsers.stream().filter(user ->
//                    user.getRole().name().equals("ADMIN")).count();
//            long pendingPublishers = allUsers.stream().filter(user ->
//                    user.getRole().name().equals("ADMIN") &&
//                            user.getStatus().name().equals("INACTIVE")).count();
//
//            response.put("success", true);
//            response.put("totalUsers", totalUsers);
//            response.put("activeUsers", activeUsers);
//            response.put("totalBooks", 0); // Add book service later
//            response.put("pendingBooks", 0);
//            response.put("totalPublishers", totalPublishers);
//            response.put("pendingPublishers", pendingPublishers);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//
//    // ✅ Get All Users
//    @GetMapping("/users")
//    public ResponseEntity<Map<String, Object>> getAllUsers(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int limit) {
//        try {
//            List<User> allUsers = userService.getAllUser();
//
//            // Simple pagination
//            int start = (page - 1) * limit;
//            int end = Math.min(start + limit, allUsers.size());
//            List<User> paginatedUsers = allUsers.subList(start, end);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("users", convertUsersToDTO(paginatedUsers));
//            response.put("totalPages", (int) Math.ceil((double) allUsers.size() / limit));
//            response.put("currentPage", page);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//
//    // ✅ Get User by ID
//    @GetMapping("/users/{userId}")
//    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable int userId) {
//        try {
//            User user = userService.getUserById(userId);
//
//            if (user == null) {
//                Map<String, Object> errorResponse = new HashMap<>();
//                errorResponse.put("success", false);
//                errorResponse.put("message", "User not found");
//                return ResponseEntity.status(404).body(errorResponse);
//            }
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("user", convertUserToDetailedDTO(user));
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//
//    // ✅ Approve/Reject Publisher
//    @PutMapping("/publishers/{userId}/approve")
//    public ResponseEntity<Map<String, Object>> approvePublisher(
//            @PathVariable int userId,
//            @RequestBody Map<String, Boolean> request) {
//        try {
//            boolean approve = request.get("approve");
//            String result;
//
//            if (approve) {
//                result = userService.approveAdmin(userId);
//            } else {
//                result = userService.rejectAdmin(userId);
//            }
//
//            Map<String, Object> response = new HashMap<>();
//            if (result.startsWith("✅")) {
//                response.put("success", true);
//                response.put("message", result);
//            } else {
//                response.put("success", false);
//                response.put("message", result);
//            }
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//
//    // ✅ Delete User
//    @DeleteMapping("/users/{userId}")
//    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable int userId) {
//        try {
//            String result = userService.deleteUserById(userId);
//
//            Map<String, Object> response = new HashMap<>();
//            if (result.equals("User Deleted Successfully")) {
//                response.put("success", true);
//                response.put("message", result);
//            } else {
//                response.put("success", false);
//                response.put("message", result);
//            }
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//
//    // ✅ Helper methods
//    private List<Map<String, Object>> convertUsersToDTO(List<User> users) {
//        return users.stream().map(this::convertUserToDTO).collect(Collectors.toList());
//    }
//
//    private Map<String, Object> convertUserToDTO(User user) {
//        Map<String, Object> dto = new HashMap<>();
//        dto.put("_id", user.getId());
//        dto.put("fullName", user.getName());
//        dto.put("email", user.getEmail());
//        dto.put("role", user.getRole().name().toLowerCase());
//        dto.put("isApproved", user.getStatus().name().equals("ACTIVE"));
//        dto.put("isActive", user.getStatus().name().equals("ACTIVE"));
//        dto.put("createdAt", user.getCreatedDate());
//        return dto;
//    }
//
//    private Map<String, Object> convertUserToDetailedDTO(User user) {
//        Map<String, Object> dto = convertUserToDTO(user);
//        // Add additional fields if needed
//        dto.put("preferredLanguage", user.getPreferredLanguage());
//        dto.put("lastLogin", user.getCreatedDate()); // You can add lastLogin field later
//        return dto;
//    }
//}