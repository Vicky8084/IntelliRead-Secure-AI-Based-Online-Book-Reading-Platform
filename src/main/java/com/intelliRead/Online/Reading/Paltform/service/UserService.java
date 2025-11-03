package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.UserConverter;
import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.enums.Status;
import com.intelliRead.Online.Reading.Paltform.exception.UserAlreadyExistException;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.UserRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    UserRepository userRepository;
    EmailService emailService;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       EmailService emailService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ FIXED: Return User object instead of String
    public User addUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistException("User already exists!");
        }

        User user = UserConverter.convertUserRequestDtoIntoUser(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.getPasswordHash()));

        User savedUser;

        if (dto.getRole() == Role.ROLE) {
            user.setStatus(Status.INACTIVE);
            savedUser = userRepository.save(user);
            emailService.sendAdminApprovalRequest(savedUser);
        } else {
            user.setStatus(Status.ACTIVE);
            savedUser = userRepository.save(user);
            emailService.sendWelcomeEmail(savedUser);
        }

        return savedUser; // ✅ Return User object
    }

    public String approveAdmin(int userId) {
        User user = getUserById(userId);
        if (user != null && user.getRole() == Role.ROLE) {
            user.setStatus(Status.ACTIVE);
            userRepository.save(user);
            emailService.sendAdminApproved(user);
            return "✅ Admin approved successfully!";
        }
        return "❌ User not found or not an admin!";
    }

    public String rejectAdmin(int userId) {
        User user = getUserById(userId);
        if (user != null && user.getRole() == Role.ROLE) {
            emailService.sendAdminRejected(user);
            userRepository.delete(user);
            return "✅ Admin registration rejected!";
        }
        return "❌ User not found or not an admin!";
    }

    public User getUserById(int id){
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }

    public User getUserByEmail(String email){
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        return userOptional.orElse(null);
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public String deleteUserById(int id){
        User user = getUserById(id);
        if(user != null){
            userRepository.deleteById(id);
            return "User Deleted Successfully";
        } else {
            return "User not found";
        }
    }

    public String updateUser(int id, UserRequestDTO userRequestDTO){
        User user = getUserById(id);
        if(user != null){
            user.setName(userRequestDTO.getName());
            user.setEmail(userRequestDTO.getEmail());

            if(userRequestDTO.getPasswordHash() != null && !userRequestDTO.getPasswordHash().isEmpty()) {
                user.setPasswordHash(passwordEncoder.encode(userRequestDTO.getPasswordHash()));
            }

            user.setRole(userRequestDTO.getRole());
            user.setPreferredLanguage(userRequestDTO.getPreferredLanguage());
            userRepository.save(user);
            return "User Updated Successfully";
        } else {
            return "User not found";
        }
    }
}