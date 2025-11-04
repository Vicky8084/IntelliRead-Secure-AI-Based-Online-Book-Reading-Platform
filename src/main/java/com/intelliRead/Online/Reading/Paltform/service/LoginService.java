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

    // Fixed admin emails - yeh 5 hi ADMIN hain, koi aur nahi ban sakta
    private final List<String> ADMIN_EMAILS = Arrays.asList(
            "noreply.intelliread@gmail.com",
            "mrvg4545@gmail.com",
            "aaarti.rcc090@gmail.com",
            "jarpit0103@gmail.com",
            "rwi.sharma001@gmail.com"
    );

    public LoginResponseDTO login(LoginRequestDto loginRequestDTO) {
        try {
            System.out.println("🔐 Login attempt for: " + loginRequestDTO.getEmail());

            Optional<User> optionalUser = userRepository.findUserByEmail(loginRequestDTO.getEmail());
            if (optionalUser.isEmpty()) {
                System.out.println("❌ User not found: " + loginRequestDTO.getEmail());
                return new LoginResponseDTO(null, null, null, 0, null, "Invalid email or password", false, null);
            }

            User user = optionalUser.get();
            System.out.println("✅ User found: " + user.getEmail() + " | Role: " + user.getRole() + " | Status: " + user.getStatus());

            // Check if account is active
            // ✅ ADD this status check in login() method
            if (user.getStatus() != Status.ACTIVE) {
                System.out.println("❌ Account not active: " + user.getEmail());
                return new LoginResponseDTO(null, null, null, 0, null,
                        user.getRole() == Role.PUBLISHER ?
                                "Publisher account pending admin approval" : "Account is inactive",
                        false, null);
            }

            // ✅ Check if user is admin by EMAIL (yeh fixed hai)
            boolean isAdmin = ADMIN_EMAILS.contains(loginRequestDTO.getEmail().toLowerCase());

            if (isAdmin) {
                System.out.println("👑 Admin login detected: " + loginRequestDTO.getEmail());
                // For admin users, manually verify password
                if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPasswordHash())) {
                    System.out.println("❌ Admin password mismatch for: " + loginRequestDTO.getEmail());
                    return new LoginResponseDTO(null, null, null, 0, null, "Invalid email or password", false, null);
                }
                System.out.println("✅ Admin password verified: " + loginRequestDTO.getEmail());
            } else {
                // For non-admin users, use Spring Security authentication
                System.out.println("🔐 Authenticating non-admin user: " + loginRequestDTO.getEmail());
                try {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
                    );
                    System.out.println("✅ Non-admin authentication successful: " + loginRequestDTO.getEmail());
                } catch (Exception e) {
                    System.out.println("❌ Non-admin authentication failed: " + e.getMessage());
                    return new LoginResponseDTO(null, null, null, 0, null, "Invalid email or password", false, null);
                }
            }

            // Generate JWT Token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getEmail());
            final String jwtToken = jwtUtil.generateToken(loginRequestDTO.getEmail());
            System.out.println("✅ JWT Token generated for: " + loginRequestDTO.getEmail());

            // ✅ CRITICAL FIX: Determine redirect URL based on ROLE and EMAIL
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

    // In determineRedirectUrl method - REPLACE ENTIRE METHOD:
    private String determineRedirectUrl(String email, Role role) {
        // ✅ CRITICAL FIX: Check admin by EMAIL first (fixed 5 emails), then by role
        boolean isAdmin = ADMIN_EMAILS.contains(email.toLowerCase());

        System.out.println("🎯 Redirect Decision:");
        System.out.println("   📧 Email: " + email);
        System.out.println("   🔑 Role: " + role);
        System.out.println("   👑 Is Admin: " + isAdmin);

        // ✅ ADMIN USERS: Always go to admin dashboard (fixed 5 emails)
        if (isAdmin) {
            System.out.println("   🚀 Redirecting to ADMIN DASHBOARD");
            return "/admin-dashboard";
        }

        // ✅ PUBLISHERS: Go to publisher dashboard
        if (role == Role.PUBLISHER) {
            System.out.println("   📚 Redirecting to PUBLISHER DASHBOARD");
            return "/publisher-dashboard";
        }

        // ✅ USERS: Go to bookscreen
        if (role == Role.USER) {
            System.out.println("   👤 Redirecting to BOOKSCREEN");
            return "/bookscreen";
        }

        // ✅ DEFAULT: Home page
        System.out.println("   🏠 Redirecting to HOME");
        return "/Home";
    }
}