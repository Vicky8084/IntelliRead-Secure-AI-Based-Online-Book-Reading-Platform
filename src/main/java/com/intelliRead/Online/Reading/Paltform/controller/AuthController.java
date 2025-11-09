package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.requestDTO.UserRequestDTO;
import com.intelliRead.Online.Reading.Paltform.responseDTO.RegistrationResponseDTO;
import com.intelliRead.Online.Reading.Paltform.service.PasswordResetService;
import com.intelliRead.Online.Reading.Paltform.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDTO> register(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            // ✅ Additional validation for ADMIN role
            if (userRequestDTO.getRole() == Role.ADMIN) {
                return ResponseEntity.badRequest().body(
                        new RegistrationResponseDTO(0, null, null, null,
                                "Admin registration not allowed", false)
                );
            }

            RegistrationResponseDTO response = registrationService.registerUser(userRequestDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            RegistrationResponseDTO errorResponse = new RegistrationResponseDTO(
                    0, null, null, null, e.getMessage(), false
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("Email is required"));
        }

        try {
            String result = passwordResetService.generateResetToken(email);

            if (result.startsWith("✅")) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Password reset OTP sent to your email");
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse(result));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to process password reset request"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        String token = request.get("token");

        if (email == null || email.trim().isEmpty() ||
                newPassword == null || newPassword.trim().isEmpty() ||
                token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("All fields are required"));
        }

        try {
            // Validate token first
            if (!passwordResetService.validateToken(token)) {
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid or expired OTP"));
            }

            String result = passwordResetService.resetPassword(token, newPassword);

            if (result.startsWith("✅")) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Password reset successfully");
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse(result));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to reset password"));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String email = request.get("email");

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("OTP is required"));
        }

        try {
            boolean isValid = passwordResetService.validateToken(token);

            if (isValid) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "OTP verified successfully");
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid or expired OTP"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to verify OTP"));
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("status", "error");
        return response;
    }
}