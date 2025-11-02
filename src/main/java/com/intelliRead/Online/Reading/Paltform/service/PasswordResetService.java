//package com.intelliRead.Online.Reading.Paltform.service;
//
//import com.intelliRead.Online.Reading.Paltform.model.PasswordResetToken;
//import com.intelliRead.Online.Reading.Paltform.model.User;
//import com.intelliRead.Online.Reading.Paltform.repository.PasswordResetTokenRepository;
//import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//public class PasswordResetService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordResetTokenRepository tokenRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private EmailService emailService;
//
//    public String generateResetToken(String email) {
//        Optional<User> userOptional = userRepository.findUserByEmail(email);
//        if (userOptional.isEmpty()) {
//            return "❌ No user found with this email address.";
//        }
//
//        User user = userOptional.get();
//
//        // Invalidate existing tokens
//        tokenRepository.findByUserAndUsedFalse(user).ifPresent(existingToken -> {
//            existingToken.setUsed(true);
//            tokenRepository.save(existingToken);
//        });
//
//        // Generate new token
//        String token = UUID.randomUUID().toString();
//        PasswordResetToken resetToken = new PasswordResetToken(token, user);
//        tokenRepository.save(resetToken);
//
//        // Send email
//        emailService.sendPasswordResetEmail(user, token);
//
//        return "✅ Password reset link has been sent to your email.";
//    }
//
//    public String resetPassword(String token, String newPassword) {
//        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);
//
//        if (tokenOptional.isEmpty()) {
//            return "❌ Invalid or expired reset token.";
//        }
//
//        PasswordResetToken resetToken = tokenOptional.get();
//
//        if (!resetToken.isValid()) {
//            return "❌ Reset token has expired or has already been used.";
//        }
//
//        // Update password
//        User user = resetToken.getUser();
//        user.setPasswordHash(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//
//        // Mark token as used
//        resetToken.setUsed(true);
//        tokenRepository.save(resetToken);
//
//        // ✅ Send success email
//        try {
//            emailService.sendPasswordResetSuccessEmail(user);
//        } catch (Exception e) {
//            System.err.println("Failed to send password reset success email: " + e.getMessage());
//        }
//
//        return "✅ Password has been reset successfully. You can now login with your new password.";
//    }
//
//    public boolean validateToken(String token) {
//        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);
//        return tokenOptional.isPresent() && tokenOptional.get().isValid();
//    }
//}