package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.model.PasswordResetToken;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.PasswordResetTokenRepository;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public String generateResetToken(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isEmpty()) {
            return "❌ No user found with this email address.";
        }

        User user = userOptional.get();

        // Invalidate existing tokens
        tokenRepository.findByUserAndUsedFalse(user).ifPresent(existingToken -> {
            existingToken.setUsed(true);
            tokenRepository.save(existingToken);
        });

        // Generate 6-digit OTP instead of UUID for frontend compatibility
        String token = generateSixDigitOTP();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        // Set shorter expiry for OTP (10 minutes)
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        tokenRepository.save(resetToken);

        // Send email with OTP
        try {
            emailService.sendPasswordResetOTPEmail(user, token);
            return "✅ Password reset OTP has been sent to your email.";
        } catch (Exception e) {
            // Delete the token if email fails
            tokenRepository.delete(resetToken);
            return "❌ Failed to send email. Please try again.";
        }
    }

    public String resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            return "❌ Invalid or expired OTP.";
        }

        PasswordResetToken resetToken = tokenOptional.get();

        if (!resetToken.isValid()) {
            return "❌ OTP has expired or has already been used.";
        }

        // Update password
        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        // Send success email
        try {
            emailService.sendPasswordResetSuccessEmail(user);
        } catch (Exception e) {
            System.err.println("Failed to send password reset success email: " + e.getMessage());
        }

        return "✅ Password has been reset successfully. You can now login with your new password.";
    }

    public boolean validateToken(String token) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);
        return tokenOptional.isPresent() && tokenOptional.get().isValid();
    }

    // Generate 6-digit OTP for frontend compatibility
    private String generateSixDigitOTP() {
        return String.valueOf(1000 + (int)(Math.random() * 9000));
    }

    // Clean up expired tokens every hour
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}