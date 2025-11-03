package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.requestDTO.LoginRequestDto;
import com.intelliRead.Online.Reading.Paltform.responseDTO.LoginResponseDTO;
import com.intelliRead.Online.Reading.Paltform.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/admin")
@CrossOrigin(origins = "*")
public class AdminAuthController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> adminLogin(@RequestBody LoginRequestDto loginRequestDTO) {
        System.out.println("🔐 Admin login endpoint called: /auth/admin/login");
        System.out.println("📧 Email: " + loginRequestDTO.getEmail());

        LoginResponseDTO response = loginService.login(loginRequestDTO);

        // ✅ Force redirect to admin-dashboard for admin login
        if (response.isSuccess()) {
            response.setRedirectUrl("/admin-dashboard");
            System.out.println("🔄 Setting redirect URL to /admin-dashboard");
        }

        return ResponseEntity.ok(response);
    }
}