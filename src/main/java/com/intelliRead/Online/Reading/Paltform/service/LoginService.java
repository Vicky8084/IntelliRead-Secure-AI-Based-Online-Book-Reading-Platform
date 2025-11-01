package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.enums.Status;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.LoginRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String login(LoginRequestDto loginRequestDTO) {

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
