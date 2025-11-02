package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.enums.Status;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.requestDTO.UserRequestDTO;
import com.intelliRead.Online.Reading.Paltform.responseDTO.RegistrationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserService userService;

    @Autowired
    public RegistrationService(UserService userService) {
        this.userService = userService;
    }

    public RegistrationResponseDTO registerUser(UserRequestDTO userDTO) {
        User savedUser = userService.addUser(userDTO);

        String message;
        if (savedUser.getRole() == Role.ADMIN && savedUser.getStatus() == Status.INACTIVE) {
            message = "Admin registration pending approval! Please wait for admin approval.";
        } else {
            message = "Registration successful! Please login to continue.";
        }

        return new RegistrationResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                message,
                true
        );
    }
}