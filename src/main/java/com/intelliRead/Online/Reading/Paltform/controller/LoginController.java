package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.requestDTO.LoginRequestDto;
import com.intelliRead.Online.Reading.Paltform.responseDTO.LoginResponseDTO;
import com.intelliRead.Online.Reading.Paltform.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:8080", "http://127.0.0.1:8080"})
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDto loginRequestDTO) {
        LoginResponseDTO response = loginService.login(loginRequestDTO);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/simple-login")
    public ResponseEntity<String> simpleLogin(@RequestBody LoginRequestDto loginRequestDTO) {
        String response = loginService.simpleLogin(loginRequestDTO);
        if (response.startsWith("✅")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/create-test-user")
    public ResponseEntity<String> createTestUser() {
        String result = loginService.createTestUser();
        return ResponseEntity.ok(result);
    }

    // ✅ ADD THIS TEST ENDPOINT
    @GetMapping("/test-connection")
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok("✅ Backend is running! Connection successful at: " + new java.util.Date());
    }
}