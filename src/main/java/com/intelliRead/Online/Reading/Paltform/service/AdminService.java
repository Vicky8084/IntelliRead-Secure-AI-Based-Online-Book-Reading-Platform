package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.enums.Status;
import com.intelliRead.Online.Reading.Paltform.exception.UserAlreadyExistException;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.AdminRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${admin.registration.secret:ADMIN_SECRET_2024}")
    private String adminSecretKey;

    public String registerAdmin(AdminRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistException("Admin user already exists!");
        }

        // Validate admin secret key
        if (!adminSecretKey.equals(dto.getAdminSecretKey())) {
            return "❌ Invalid admin secret key!";
        }

        User admin = new User();
        admin.setName(dto.getName());
        admin.setEmail(dto.getEmail());
        admin.setPasswordHash(passwordEncoder.encode(dto.getPasswordHash()));
        admin.setRole(Role.ADMIN);
        admin.setStatus(Status.ACTIVE);
        admin.setPreferredLanguage(dto.getPreferredLanguage());

        userRepository.save(admin);

        // Send admin welcome email
        emailService.sendAdminWelcomeEmail(admin);

        return "✅ Admin registered successfully!";
    }
}