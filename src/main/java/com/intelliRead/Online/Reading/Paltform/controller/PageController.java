package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class PageController {

    @Autowired
    private UserRepository userRepository;

    // List of fixed admin emails
    private final List<String> ADMIN_EMAILS = Arrays.asList(
            "noreply.intelliread@gmail.com",
            "admin1.intelliread@gmail.com",
            "admin2.intelliread@gmail.com",
            "admin3.intelliread@gmail.com",
            "admin4.intelliread@gmail.com"
    );

    // ✅ Home Page
    @GetMapping("/")
    public String home() {
        return "Home";
    }

    @GetMapping("/Home")
    public String homePage() {
        return "Home";
    }

    // ✅ Auth Pages
    @GetMapping("/login")
    public String loginPage() {
        return "Login";
    }

    @GetMapping("/Login")
    public String loginPage2() {
        return "Login";
    }

    @GetMapping("/signup")
    public String registerPage() {
        return "SignUp";
    }

    @GetMapping("/SignUp")
    public String registerPage2() {
        return "SignUp";
    }

    @GetMapping("/forgotpassword")
    public String forgotPasswordPage() {
        return "ForgotPass";
    }

    @GetMapping("/ForgotPass")
    public String forgotPasswordPage2() {
        return "ForgotPass";
    }

    // ✅ Admin Login Page (Separate route) - DIRECT ACCESS
    @GetMapping("/admin-login")
    public String adminLoginPage() {
        return "Admin"; // This opens Admin.html page
    }

    // ✅ Admin Dashboard - With Simple Session Check
    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpServletRequest request, Model model) {
        System.out.println("🔄 Admin Dashboard accessed");

        // Simple session check - check if user is logged in
        Boolean isLoggedIn = (Boolean) request.getSession().getAttribute("isLoggedIn");
        String userEmail = (String) request.getSession().getAttribute("userEmail");

        System.out.println("📧 Session userEmail: " + userEmail);
        System.out.println("🔐 Session isLoggedIn: " + isLoggedIn);

        // If not logged in via session, check localStorage data (for testing)
        if (isLoggedIn == null || userEmail == null) {
            System.out.println("⚠️ No session found, checking if user is accessing directly");
            // Allow access for testing, but show warning
            model.addAttribute("warning", "Please login first for full functionality");
        }

        // Check if user is admin
        boolean isAdmin = userEmail != null && ADMIN_EMAILS.contains(userEmail.toLowerCase());
        System.out.println("👑 Is Admin: " + isAdmin);

        // Add admin user details to model
        if (userEmail != null) {
            Optional<User> userOptional = userRepository.findUserByEmail(userEmail);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("admin", user);
                System.out.println("✅ User data added to model: " + user.getName());
            } else {
                // Add default admin for testing
                User defaultAdmin = new User();
                defaultAdmin.setName("Admin User");
                defaultAdmin.setEmail(userEmail != null ? userEmail : "noreply.intelliread@gmail.com");
                model.addAttribute("admin", defaultAdmin);
                System.out.println("ℹ️ Default admin data added to model");
            }
        } else {
            // Add default admin for direct access
            User defaultAdmin = new User();
            defaultAdmin.setName("Administrator");
            defaultAdmin.setEmail("noreply.intelliread@gmail.com");
            model.addAttribute("admin", defaultAdmin);
            System.out.println("ℹ️ Default admin data added for direct access");
        }

        return "admin-dashboard";
    }

    // ✅ Admin Logout and Session Management
    @GetMapping("/admin-logout")
    public String adminLogout(HttpServletRequest request) {
        System.out.println("🚪 Admin logout requested");

        // Clear session
        request.getSession().invalidate();
        System.out.println("✅ Session cleared");

        return "redirect:/admin-login";
    }

    // ✅ Admin Session Setter (for testing)
    @GetMapping("/admin-set-session")
    public String setAdminSession(HttpServletRequest request) {
        System.out.println("🔧 Setting admin session for testing");

        request.getSession().setAttribute("isLoggedIn", true);
        request.getSession().setAttribute("userEmail", "noreply.intelliread@gmail.com");
        request.getSession().setAttribute("userRole", "ADMIN");

        System.out.println("✅ Admin session set for: noreply.intelliread@gmail.com");
        return "redirect:/admin-dashboard";
    }

    // ✅ Additional pages
    @GetMapping("/books")
    public String books() {
        return "books";
    }

    @GetMapping("/bookscreen")
    public String bookScreen() {
        return "bookscreen";
    }

    @GetMapping("/publisher-dashboard")
    public String publisherDashboard() {
        return "publisher-dashboard";
    }

    // ✅ Publisher Route
    @GetMapping("/publisher")
    public String publisher() {
        return "publisher-dashboard";
    }

    // Helper method to check if current user is admin
    private boolean isAdminUser(HttpServletRequest request) {
        String userEmail = getCurrentUserEmail(request);
        return userEmail != null && ADMIN_EMAILS.contains(userEmail.toLowerCase());
    }

    // Helper method to get current user email from multiple sources
    private String getCurrentUserEmail(HttpServletRequest request) {
        // 1. Try to get from session
        Object sessionEmail = request.getSession().getAttribute("userEmail");
        if (sessionEmail != null) {
            System.out.println("📧 Email from session: " + sessionEmail);
            return sessionEmail.toString();
        }

        // 2. Try to get from JWT token (if implemented)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            System.out.println("🔑 JWT token found in header");
            // In real implementation, decode JWT token here
        }

        // 3. Try to get from request parameter
        String emailParam = request.getParameter("email");
        if (emailParam != null && !emailParam.trim().isEmpty()) {
            System.out.println("📧 Email from parameter: " + emailParam);
            return emailParam;
        }

        // 4. Try to get from localStorage (via JavaScript)
        // This would require JavaScript to send the email in a header or parameter

        System.out.println("❌ No user email found");
        return null;
    }
}