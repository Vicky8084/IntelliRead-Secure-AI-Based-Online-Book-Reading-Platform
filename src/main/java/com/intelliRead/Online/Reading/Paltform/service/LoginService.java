package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.config.JwtUtil;
import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.enums.Status;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.LoginRequestDto;
import com.intelliRead.Online.Reading.Paltform.responseDTO.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // List of fixed admin emails
    private final List<String> ADMIN_EMAILS = Arrays.asList(
            "noreply.intelliread@gmail.com",
            "admin1.intelliread@gmail.com",
            "admin2.intelliread@gmail.com",
            "admin3.intelliread@gmail.com",
            "admin4.intelliread@gmail.com"
    );

    public LoginResponseDTO login(LoginRequestDto loginRequestDTO) {
        try {
            System.out.println("🔐 Login attempt for: " + loginRequestDTO.getEmail());

            // Input validation
            if (loginRequestDTO.getEmail() == null || loginRequestDTO.getEmail().trim().isEmpty()) {
                return new LoginResponseDTO(null, null, null, 0, null, "Email is required", false, null);
            }

            if (loginRequestDTO.getPassword() == null || loginRequestDTO.getPassword().isEmpty()) {
                return new LoginResponseDTO(null, null, null, 0, null, "Password is required", false, null);
            }

            String email = loginRequestDTO.getEmail().trim().toLowerCase();

            Optional<User> optionalUser = userRepository.findUserByEmail(email);
            if (optionalUser.isEmpty()) {
                System.out.println("❌ User not found: " + email);
                return new LoginResponseDTO(null, null, null, 0, null, "Invalid email or password", false, null);
            }

            User user = optionalUser.get();
            System.out.println("✅ User found: " + user.getEmail() + " | Role: " + user.getRole() + " | Status: " + user.getStatus());

            // Check if account is active
            if (user.getStatus() != Status.ACTIVE) {
                System.out.println("❌ Account not active: " + user.getEmail());
                return new LoginResponseDTO(null, null, null, 0, null,
                        user.getRole() == Role.ROLE ?
                                "Publisher account pending admin approval" : "Account is inactive", false, null);
            }

            // ✅ Check if user is admin
            boolean isAdmin = ADMIN_EMAILS.contains(email);

            // For ALL users (admin and non-admin), manually verify password
            // This avoids AuthenticationManager issues
            if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPasswordHash())) {
                System.out.println("❌ Password mismatch for: " + email);
                return new LoginResponseDTO(null, null, null, 0, null, "Invalid email or password", false, null);
            }

            System.out.println("✅ Password verified for: " + email);

            if (isAdmin) {
                System.out.println("👑 Admin login successful: " + email);
            } else {
                System.out.println("✅ Non-admin login successful: " + email);
            }

            // Generate JWT Token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            final String jwtToken = jwtUtil.generateToken(email);
            System.out.println("✅ JWT Token generated for: " + email);

            // Determine redirect URL based on user type
            String redirectUrl = determineRedirectUrl(user.getEmail(), user.getRole());
            System.out.println("🔄 Redirect URL determined: " + redirectUrl);

            return new LoginResponseDTO(
                    jwtToken,
                    user.getEmail(),
                    user.getRole(),
                    user.getId(),
                    user.getName(),
                    "Login successful",
                    true,
                    redirectUrl
            );

        } catch (Exception e) {
            System.out.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            return new LoginResponseDTO(null, null, null, 0, null, "Login failed: " + e.getMessage(), false, null);
        }
    }

    private String determineRedirectUrl(String email, Role role) {
        // ✅ Check admin by EMAIL, not by role
        boolean isAdmin = ADMIN_EMAILS.contains(email.toLowerCase());

        System.out.println("🎯 Redirect Decision:");
        System.out.println("   📧 Email: " + email);
        System.out.println("   🔑 Role: " + role);
        System.out.println("   👑 Is Admin: " + isAdmin);

        if (isAdmin) {
            System.out.println("   🚀 Redirecting to ADMIN DASHBOARD");
            return "/admin-dashboard"; // Admin users go to admin dashboard
        } else if (role == Role.ROLE) {
            System.out.println("   📚 Redirecting to PUBLISHER DASHBOARD");
            return "/publisher-dashboard"; // Regular publishers
        } else {
            System.out.println("   👤 Redirecting to BOOKSCREEN");
            return "/bookscreen"; // Regular users
        }
    }

    private boolean isAdminUser(String email) {
        return ADMIN_EMAILS.contains(email.toLowerCase());
    }
}