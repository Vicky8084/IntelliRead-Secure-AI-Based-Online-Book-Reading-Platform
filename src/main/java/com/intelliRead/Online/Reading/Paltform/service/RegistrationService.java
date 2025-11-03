package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.enums.Status;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.UserRequestDTO;
import com.intelliRead.Online.Reading.Paltform.responseDTO.RegistrationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public RegistrationResponseDTO registerUser(UserRequestDTO userRequestDTO) {
        // Check if user already exists
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            return new RegistrationResponseDTO(0, null, null, null, "User already exists with this email", false);
        }

        // Create new user
        User user = new User();
        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userRequestDTO.getPasswordHash()));
        user.setRole(userRequestDTO.getRole());
        user.setPreferredLanguage(userRequestDTO.getPreferredLanguage());

        // Set status based on role
        if (userRequestDTO.getRole() == Role.ROLE) {
            user.setStatus(Status.INACTIVE); // Admin needs approval
        } else {
            user.setStatus(Status.ACTIVE); // User is active immediately
        }

        User savedUser = userRepository.save(user);

        // Send appropriate email
        if (userRequestDTO.getRole() == Role.ROLE) {
            emailService.sendAdminApprovalRequest(savedUser);
            return new RegistrationResponseDTO(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getRole(),
                    "Publisher account created successfully! Please wait for Admin approval.",
                    true
            );
        } else {
            emailService.sendWelcomeEmail(savedUser);
            return new RegistrationResponseDTO(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getRole(),
                    "User account created successfully!",
                    true
            );
        }
    }
}