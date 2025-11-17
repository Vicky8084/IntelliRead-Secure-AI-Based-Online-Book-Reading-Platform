package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    // ✅ Admin Login Page - SIRF EK METHOD HONI CHAHIYE
    @GetMapping("/admin-login")
    public String adminLoginPage(HttpServletRequest request) {
        System.out.println("🔐 Admin Login Page Accessed");

        // ✅ Check if already logged in as admin
        String userEmail = (String) request.getSession().getAttribute("userEmail");

        if (userEmail != null && ADMIN_EMAILS.contains(userEmail.toLowerCase())) {
            System.out.println("✅ Already logged in as admin, redirecting to dashboard");
            return "redirect:/admin-dashboard";
        }

        return "Admin"; // This opens Admin.html page
    }

    // ✅ Admin Dashboard - TEMPORARY FIX: Session check disable karo
    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpServletRequest request, Model model) {
        System.out.println("🔄 Admin Dashboard accessed - TEMPORARY ACCESS");

        // ✅ TEMPORARY: Session check comment out karo
        /*
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("❌ NO SESSION: Redirecting to admin login");
            return "redirect:/admin-login";
        }

        String userEmail = (String) session.getAttribute("userEmail");
        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");

        if (isLoggedIn == null || userEmail == null || !ADMIN_EMAILS.contains(userEmail.toLowerCase())) {
            System.out.println("❌ UNAUTHORIZED ACCESS: Invalid admin session");
            return "redirect:/admin-login";
        }
        */

        System.out.println("✅ TEMPORARY ACCESS: Admin dashboard granted");

        // Add default admin data
        User defaultAdmin = new User();
        defaultAdmin.setName("Administrator");
        defaultAdmin.setEmail("noreply.intelliread@gmail.com");
        model.addAttribute("admin", defaultAdmin);

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

    // ✅ NEW: Book Details Page - Ye add karna important hai
    @GetMapping("/book-details")
    public String bookDetailsPage(@RequestParam("bookId") int bookId,
                                  HttpServletRequest request,
                                  Model model) {
        System.out.println("📖 Book Details Page accessed for book ID: " + bookId);

        // Check session
        Boolean isLoggedIn = (Boolean) request.getSession().getAttribute("isLoggedIn");
        String userEmail = (String) request.getSession().getAttribute("userEmail");

        System.out.println("📧 Session userEmail: " + userEmail);

        if (isLoggedIn == null || userEmail == null) {
            System.out.println("⚠️ No session found for book details");
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

        // Add bookId to model
        model.addAttribute("bookId", bookId);
        System.out.println("✅ Book ID added to model: " + bookId);

        return "book-details"; // This will return book-details.html from templates folder
    }

    // ✅ Book Reader Page - Custom PDF Reader with AI
    @GetMapping("/book-reader")
    public String bookReaderPage(@RequestParam("bookId") int bookId,
                                 HttpServletRequest request,
                                 Model model) {
        System.out.println("📖 Book Reader accessed for book ID: " + bookId);

        // Check session
        Boolean isLoggedIn = (Boolean) request.getSession().getAttribute("isLoggedIn");
        String userEmail = (String) request.getSession().getAttribute("userEmail");

        if (isLoggedIn == null || userEmail == null) {
            System.out.println("⚠️ No session found for book reader");
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

        // Add bookId to model
        model.addAttribute("bookId", bookId);
        System.out.println("✅ Book ID added to model: " + bookId);

        return "book-reader"; // This will return book-reader.html from templates folder
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

    // ✅ Admin Session Setter (for testing) - ISKO REMOVE KARO PRODUCTION MEIN
    @GetMapping("/admin-set-session")
    public String setAdminSession(HttpServletRequest request) {
        System.out.println("🔧 Setting admin session for testing");

        request.getSession().setAttribute("isLoggedIn", true);
        request.getSession().setAttribute("userEmail", "noreply.intelliread@gmail.com");
        request.getSession().setAttribute("userRole", "ADMIN");

        System.out.println("✅ Admin session set for: noreply.intelliread@gmail.com");
        return "redirect:/admin-dashboard";
    }

    // ✅ Publisher Session Setter (for testing) - ISKO BHI REMOVE KARO
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

    // ✅ TEMPORARY DEBUG METHOD - Session check ke liye
    @GetMapping("/debug-session")
    public ResponseEntity<Map<String, Object>> debugSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        Map<String, Object> sessionInfo = new java.util.HashMap<>();

        if (session != null) {
            sessionInfo.put("sessionId", session.getId());
            sessionInfo.put("userEmail", session.getAttribute("userEmail"));
            sessionInfo.put("isLoggedIn", session.getAttribute("isLoggedIn"));
            sessionInfo.put("creationTime", new java.util.Date(session.getCreationTime()));
            sessionInfo.put("lastAccessed", new java.util.Date(session.getLastAccessedTime()));

            // Print all attributes
            java.util.Enumeration<String> attributeNames = session.getAttributeNames();
            java.util.List<String> attributes = new java.util.ArrayList<>();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                attributes.add(name + "=" + session.getAttribute(name));
            }
            sessionInfo.put("allAttributes", attributes);
        } else {
            sessionInfo.put("message", "No active session");
        }

        return ResponseEntity.ok(sessionInfo);
    }
}