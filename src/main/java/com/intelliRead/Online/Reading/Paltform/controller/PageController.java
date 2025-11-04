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

    // Fixed admin emails - yeh 5 hi ADMIN hain
    private final List<String> ADMIN_EMAILS = Arrays.asList(
            "noreply.intelliread@gmail.com",
            "mrvg4545@gmail.com",
            "aaarti.rcc090@gmail.com",
            "jarpit0103@gmail.com",
            "rwi.sharma001@gmail.com"
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

        // Check if user is admin (by email - fixed list)
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

    // ✅ Publisher Dashboard - Role-based Access
    @GetMapping("/publisher-dashboard")
    public String publisherDashboard(HttpServletRequest request, Model model) {
        System.out.println("🔄 Publisher Dashboard accessed");

        // Check session
        Boolean isLoggedIn = (Boolean) request.getSession().getAttribute("isLoggedIn");
        String userEmail = (String) request.getSession().getAttribute("userEmail");

        System.out.println("📧 Session userEmail: " + userEmail);

        if (isLoggedIn == null || userEmail == null) {
            System.out.println("⚠️ No session found for publisher dashboard");
            model.addAttribute("warning", "Please login first for full functionality");
        }

        // Get user data
        if (userEmail != null) {
            Optional<User> userOptional = userRepository.findUserByEmail(userEmail);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("publisher", user);
                System.out.println("✅ Publisher data added to model: " + user.getName());

                // Check if user is actually a publisher
                if (user.getRole().name().equals("PUBLISHER")) {
                    System.out.println("✅ Valid publisher access");
                } else {
                    System.out.println("⚠️ User is not a publisher, but accessing publisher dashboard");
                }
            } else {
                // Add default publisher for testing
                User defaultPublisher = new User();
                defaultPublisher.setName("Publisher User");
                defaultPublisher.setEmail(userEmail != null ? userEmail : "publisher@example.com");
                model.addAttribute("publisher", defaultPublisher);
                System.out.println("ℹ️ Default publisher data added to model");
            }
        } else {
            // Add default publisher for direct access
            User defaultPublisher = new User();
            defaultPublisher.setName("Publisher");
            defaultPublisher.setEmail("publisher@example.com");
            model.addAttribute("publisher", defaultPublisher);
            System.out.println("ℹ️ Default publisher data added for direct access");
        }

        return "publisher-dashboard";
    }

    // ✅ Book Screen for Users
    @GetMapping("/bookscreen")
    public String bookScreen(HttpServletRequest request, Model model) {
        System.out.println("🔄 Book Screen accessed");

        // Check session
        Boolean isLoggedIn = (Boolean) request.getSession().getAttribute("isLoggedIn");
        String userEmail = (String) request.getSession().getAttribute("userEmail");

        System.out.println("📧 Session userEmail: " + userEmail);

        if (isLoggedIn == null || userEmail == null) {
            System.out.println("⚠️ No session found for book screen");
            model.addAttribute("warning", "Please login first for full functionality");
        }

        // Get user data
        if (userEmail != null) {
            Optional<User> userOptional = userRepository.findUserByEmail(userEmail);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("user", user);
                System.out.println("✅ User data added to model: " + user.getName());
            }
        }

        return "bookscreen";
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

    // ✅ Publisher Session Setter (for testing)
    @GetMapping("/publisher-set-session")
    public String setPublisherSession(HttpServletRequest request) {
        System.out.println("🔧 Setting publisher session for testing");

        request.getSession().setAttribute("isLoggedIn", true);
        request.getSession().setAttribute("userEmail", "publisher@example.com");
        request.getSession().setAttribute("userRole", "PUBLISHER");

        System.out.println("✅ Publisher session set for: publisher@example.com");
        return "redirect:/publisher-dashboard";
    }

    // ✅ Additional pages
    @GetMapping("/books")
    public String books() {
        return "books";
    }

    // ✅ Publisher Route
    @GetMapping("/publisher")
    public String publisher() {
        return "publisher-dashboard";
    }
}