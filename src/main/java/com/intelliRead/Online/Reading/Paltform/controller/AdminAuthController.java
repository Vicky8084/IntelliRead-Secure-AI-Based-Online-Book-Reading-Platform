package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.requestDTO.LoginRequestDto;
import com.intelliRead.Online.Reading.Paltform.responseDTO.LoginResponseDTO;
import com.intelliRead.Online.Reading.Paltform.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/auth/admin")
@CrossOrigin(origins = "*")
public class AdminAuthController {

    @Autowired
    private LoginService loginService;

    // Fixed admin emails - yeh 5 hi ADMIN hain
    private final List<String> ADMIN_EMAILS = Arrays.asList(
            "noreply.intelliread@gmail.com",
            "mrvg4545@gmail.com",
            "aaarti.rcc090@gmail.com",
            "jarpit0103@gmail.com",
            "rwi.sharma001@gmail.com"
    );

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> adminLogin(
            @RequestBody LoginRequestDto loginRequestDTO,
            HttpServletRequest request) { // ✅ HttpServletRequest add karo

        System.out.println("🔐 STRICT Admin login endpoint called: /auth/admin/login");
        System.out.println("📧 Email: " + loginRequestDTO.getEmail());

        // ✅ STRICT ADMIN ONLY CHECK
        if (!ADMIN_EMAILS.contains(loginRequestDTO.getEmail().toLowerCase())) {
            System.out.println("❌ Non-admin email trying to access admin login: " + loginRequestDTO.getEmail());
            LoginResponseDTO errorResponse = new LoginResponseDTO(
                    null, null, null, 0, null,
                    "❌ Access denied. Admin login only for authorized admin accounts.",
                    false, null
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }

        LoginResponseDTO response = loginService.login(loginRequestDTO);

        // ✅ SUCCESS: Session create karo
        if (response.isSuccess()) {
            // ✅ Server session create karo - YE LINE ADD KARO
            HttpSession session = request.getSession();
            session.setAttribute("userEmail", loginRequestDTO.getEmail());
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("userRole", "ADMIN");
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            System.out.println("✅ Admin session created for: " + loginRequestDTO.getEmail());
            System.out.println("🔐 Session ID: " + session.getId());

            // ✅ Force redirect to admin-dashboard ONLY for admin login
            response.setRedirectUrl("/admin-dashboard");
            System.out.println("🔄 Setting redirect URL to /admin-dashboard");
        }

        return ResponseEntity.ok(response);
    }
}