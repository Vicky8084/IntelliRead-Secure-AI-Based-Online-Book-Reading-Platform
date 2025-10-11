package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/password")
public class PasswordController {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public PasswordController(UserRepository userRepository,
                              BCryptPasswordEncoder passwordEncoder){
        this.passwordEncoder=passwordEncoder;
        this.userRepository=userRepository;
    }


    // 🔹 Direct Password Reset — single endpoint
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");

        Map<String, String> response = new HashMap<>();

        // Validate fields
        if (email == null || newPassword == null || confirmPassword == null) {
            response.put("status", "error");
            response.put("message", "❌ Please provide all required fields (email, newPassword, confirmPassword).");
            return ResponseEntity.badRequest().body(response);
        }

        // Check password match
        if (!newPassword.equals(confirmPassword)) {
            response.put("status", "error");
            response.put("message", "❌ Passwords do not match!");
            return ResponseEntity.badRequest().body(response);
        }

        // Find user
        Optional<User> userOpt = userRepository.findUserByEmail(email);
        if (userOpt.isEmpty()) {
            response.put("status", "error");
            response.put("message", "❌ No user found with this email!");
            return ResponseEntity.status(404).body(response);
        }

        // Update password
        User user = userOpt.get();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "✅ Password reset successful! You can now log in with your new password.");
        return ResponseEntity.ok(response);
    }
}

