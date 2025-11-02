package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.requestDTO.LoginRequestDto;
import com.intelliRead.Online.Reading.Paltform.responseDTO.LoginResponseDTO;
import com.intelliRead.Online.Reading.Paltform.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // ✅ ALLOW ALL ORIGINS
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDto loginRequestDTO) {
        LoginResponseDTO response = loginService.login(loginRequestDTO);
        return ResponseEntity.ok(response); // ✅ ALWAYS RETURN 200 OK
    }

    @GetMapping("/test-connection")
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok("✅ Backend is running! Connection successful.");
    }
}