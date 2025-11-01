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

    // ✅ NEW: JWT Login Method
    public LoginResponseDTO login(LoginRequestDto loginRequestDTO) {
        try {
            // Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword()
                    )
            );

            // If authentication successful, generate token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getEmail());
            final String jwtToken = jwtUtil.generateToken(loginRequestDTO.getEmail());

            // Get user details
            Optional<User> optionalUser = userRepository.findUserByEmail(loginRequestDTO.getEmail());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                // Check if account is ACTIVE
                if (user.getStatus() != Status.ACTIVE) {
                    return new LoginResponseDTO(null, null, null, 0, null, "Account is inactive. Please contact admin for activation.", false);
                }

                return new LoginResponseDTO(
                        jwtToken,
                        user.getEmail(),
                        user.getRole(),
                        user.getId(),
                        user.getName(),
                        "Login successful",
                        true
                );
            }

            return new LoginResponseDTO(null, null, null, 0, null, "User not found", false);

        } catch (BadCredentialsException e) {
            return new LoginResponseDTO(null, null, null, 0, null, "Invalid email or password", false);
        } catch (Exception e) {
            return new LoginResponseDTO(null, null, null, 0, null, "Login failed: " + e.getMessage(), false);
        }
    }

    // ✅ OLD: Simple Login Method (Backward Compatibility)
    public String simpleLogin(LoginRequestDto loginRequestDTO) {
        Optional<User> optionalUser = userRepository.findUserByEmail(loginRequestDTO.getEmail());
        if (optionalUser.isEmpty()) {
            return "❌ No user found with this email!";
        }

        User user = optionalUser.get();

        // Check if account is ACTIVE
        if (user.getStatus() != Status.ACTIVE) {
            return "⚠️ Account is inactive. Please contact admin for activation.";
        }

        // Check password
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPasswordHash())) {
            return "❌ Incorrect password! Please try again.";
        }

        // Success response based on role
        if (user.getRole() == Role.ADMIN) {
            return "✅ Admin login successful! Welcome, " + user.getName();
        } else {
            return "✅ User login successful! Welcome, " + user.getName();
        }
    }
}