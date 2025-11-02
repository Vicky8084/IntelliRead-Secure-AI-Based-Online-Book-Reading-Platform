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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public LoginResponseDTO login(LoginRequestDto loginRequestDTO) {
        try {
            System.out.println("🔐 Login attempt for: " + loginRequestDTO.getEmail());

            // ✅ FIRST: Manual check if user exists and password matches
            Optional<User> optionalUser = userRepository.findUserByEmail(loginRequestDTO.getEmail());
            if (optionalUser.isEmpty()) {
                System.out.println("❌ User not found: " + loginRequestDTO.getEmail());
                return new LoginResponseDTO(null, null, null, 0, null, "Invalid email or password", false);
            }

            User user = optionalUser.get();
            System.out.println("✅ User found: " + user.getEmail());
            System.out.println("📊 User status: " + user.getStatus());
            System.out.println("🔑 Stored password hash: " + user.getPasswordHash());

            // ✅ Check if account is active
            if (user.getStatus() != Status.ACTIVE) {
                System.out.println("❌ Account inactive: " + user.getEmail());
                return new LoginResponseDTO(null, null, null, 0, null, "Account is inactive. Please contact admin for activation.", false);
            }

            // ✅ MANUAL PASSWORD CHECK WITH BCRYPT
            boolean passwordMatches = passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPasswordHash());
            System.out.println("🔐 Password match result: " + passwordMatches);

            if (!passwordMatches) {
                System.out.println("❌ Password mismatch for: " + loginRequestDTO.getEmail());
                return new LoginResponseDTO(null, null, null, 0, null, "Invalid email or password", false);
            }

            System.out.println("✅ Password verified successfully!");

            // ✅ If password matches, then authenticate with Spring Security
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequestDTO.getEmail(),
                                loginRequestDTO.getPassword()
                        )
                );
                System.out.println("✅ Spring Security authentication successful");
            } catch (BadCredentialsException e) {
                System.out.println("❌ Spring Security authentication failed: " + e.getMessage());
                return new LoginResponseDTO(null, null, null, 0, null, "Invalid email or password", false);
            }

            // ✅ Generate JWT Token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getEmail());
            final String jwtToken = jwtUtil.generateToken(loginRequestDTO.getEmail());
            System.out.println("✅ JWT Token generated successfully");

            return new LoginResponseDTO(
                    jwtToken,
                    user.getEmail(),
                    user.getRole(),
                    user.getId(),
                    user.getName(),
                    "Login successful",
                    true
            );

        } catch (Exception e) {
            System.out.println("❌ Unexpected error during login: " + e.getMessage());
            e.printStackTrace();
            return new LoginResponseDTO(null, null, null, 0, null, "Login failed: " + e.getMessage(), false);
        }
    }

    public String simpleLogin(LoginRequestDto loginRequestDTO) {
        Optional<User> optionalUser = userRepository.findUserByEmail(loginRequestDTO.getEmail());
        if (optionalUser.isEmpty()) {
            return "❌ No user found with this email!";
        }

        User user = optionalUser.get();

        if (user.getStatus() != Status.ACTIVE) {
            return "⚠️ Account is inactive. Please contact admin for activation.";
        }

        // ✅ USE BCRYPT MATCHES INSTEAD OF DIRECT COMPARISON
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPasswordHash())) {
            return "❌ Incorrect password! Please try again.";
        }

        if (user.getRole() == Role.ADMIN) {
            return "✅ Admin login successful! Welcome, " + user.getName();
        } else {
            return "✅ User login successful! Welcome, " + user.getName();
        }
    }

    // ✅ TEMPORARY METHOD TO CREATE TEST USER
    public String createTestUser() {
        try {
            // Check if test user already exists
            Optional<User> existingUser = userRepository.findUserByEmail("test@example.com");
            if (existingUser.isPresent()) {
                return "Test user already exists: test@example.com / password123";
            }

            User testUser = new User();
            testUser.setName("Test User");
            testUser.setEmail("test@example.com");
            testUser.setPasswordHash(passwordEncoder.encode("password123")); // ✅ Password will be hashed
            testUser.setRole(Role.USER);
            testUser.setStatus(Status.ACTIVE);
            testUser.setPreferredLanguage("English");

            userRepository.save(testUser);
            return "✅ Test user created successfully!\nEmail: test@example.com\nPassword: password123";
        } catch (Exception e) {
            return "❌ Error creating test user: " + e.getMessage();
        }
    }
}