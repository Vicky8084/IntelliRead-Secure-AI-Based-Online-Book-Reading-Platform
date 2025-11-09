package com.intelliRead.Online.Reading.Paltform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class SessionController {

    // ✅ Fixed admin emails
    private final java.util.List<String> ADMIN_EMAILS = java.util.Arrays.asList(
            "noreply.intelliread@gmail.com",
            "mrvg4545@gmail.com",
            "aaarti.rcc090@gmail.com",
            "jarpit0103@gmail.com",
            "rwi.sharma001@gmail.com"
    );

    // ✅ Server session create karo - YE METHOD CRITICAL HAI
    @PostMapping("/set-session")
    public ResponseEntity<Map<String, String>> setAdminSession(
            @RequestBody Map<String, String> requestData,
            HttpServletRequest request) {

        String email = requestData.get("email");
        System.out.println("🔄 Setting server session for: " + email);

        if (email != null && ADMIN_EMAILS.contains(email.toLowerCase())) {
            // ✅ Server session create karo with proper attributes
            HttpSession session = request.getSession();
            session.setAttribute("userEmail", email);
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("userRole", "ADMIN");

            // ✅ Session timeout set karo (30 minutes)
            session.setMaxInactiveInterval(30 * 60);

            System.out.println("✅ Server session created for admin: " + email);
            System.out.println("🔐 Session ID: " + session.getId());
            System.out.println("📋 Session attributes: " + session.getAttributeNames());

            return ResponseEntity.ok(Map.of(
                    "message", "Session created successfully",
                    "success", "true"
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "message", "Invalid admin email",
                "success", "false"
        ));
    }

    // ✅ Check session status
    @GetMapping("/check-session")
    public ResponseEntity<Map<String, Object>> checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Don't create new session

        String userEmail = null;
        Boolean isLoggedIn = null;
        String sessionId = null;

        if (session != null) {
            userEmail = (String) session.getAttribute("userEmail");
            isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
            sessionId = session.getId();
        }

        System.out.println("🔍 Checking server session...");
        System.out.println("📧 Session userEmail: " + userEmail);
        System.out.println("🔐 Session isLoggedIn: " + isLoggedIn);
        System.out.println("🆔 Session ID: " + sessionId);

        boolean isValidAdmin = userEmail != null &&
                ADMIN_EMAILS.contains(userEmail.toLowerCase()) &&
                isLoggedIn != null && isLoggedIn;

        return ResponseEntity.ok(Map.of(
                "hasValidSession", isValidAdmin,
                "userEmail", userEmail != null ? userEmail : "null",
                "isLoggedIn", isLoggedIn != null ? isLoggedIn : false,
                "sessionId", sessionId != null ? sessionId : "no-session"
        ));
    }

    // ✅ Logout - clear server session
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        System.out.println("🚪 Clearing server session...");

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("✅ Session invalidated");
        } else {
            System.out.println("ℹ️ No active session found");
        }

        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully",
                "success", "true"
        ));
    }
}