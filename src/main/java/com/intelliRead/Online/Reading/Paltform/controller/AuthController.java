package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.requestDTO.UserRequestDTO;
import com.intelliRead.Online.Reading.Paltform.responseDTO.RegistrationResponseDTO;
import com.intelliRead.Online.Reading.Paltform.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private RegistrationService registrationService;
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDTO> register(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            // ✅ Additional validation for ADMIN role
            if (userRequestDTO.getRole() == Role.ADMIN) {
                return ResponseEntity.badRequest().body(
                        new RegistrationResponseDTO(0, null, null, null,
                                "Admin registration not allowed", false)
                );
            }

            RegistrationResponseDTO response = registrationService.registerUser(userRequestDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            RegistrationResponseDTO errorResponse = new RegistrationResponseDTO(
                    0, null, null, null, e.getMessage(), false
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}