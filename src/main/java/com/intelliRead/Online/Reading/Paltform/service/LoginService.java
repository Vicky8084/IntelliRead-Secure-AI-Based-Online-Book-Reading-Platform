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

            Optional<User> optionalUser = userRepository.findUserByEmail(loginRequestDTO.getEmail());
            if (optionalUser.isEmpty()) {
                return new LoginResponseDTO(null, null, null, 0, null, "Invalid email or password", false);
            }

            User user = optionalUser.get();

            // Check if account is active
            if (user.getStatus() != Status.ACTIVE) {
                return new LoginResponseDTO(null, null, null, 0, null,
                        user.getRole() == Role.ROLE ?
                                "Publisher account pending admin approval" : "Account is inactive", false);
            }

            // Verify password
            if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPasswordHash())) {
                return new LoginResponseDTO(null, null, null, 0, null, "Invalid email or password", false);
            }

            // Generate JWT Token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getEmail());
            final String jwtToken = jwtUtil.generateToken(loginRequestDTO.getEmail());

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
            System.out.println("❌ Login error: " + e.getMessage());
            return new LoginResponseDTO(null, null, null, 0, null, "Login failed: " + e.getMessage(), false);
        }
    }
}