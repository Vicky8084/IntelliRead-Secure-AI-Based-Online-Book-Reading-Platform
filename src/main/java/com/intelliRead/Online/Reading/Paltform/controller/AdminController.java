package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/approve/{userId}")
    public ResponseEntity<Map<String, String>> approveAdmin(@PathVariable int userId) {
        try {
            String result = userService.approveAdmin(userId);

            if (result.startsWith("✅")) {
                return ResponseEntity.ok(Map.of("message", result, "success", "true"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", result, "success", "false"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "success", "false"));
        }
    }

    @GetMapping("/reject/{userId}")
    public ResponseEntity<Map<String, String>> rejectAdmin(@PathVariable int userId) {
        try {
            String result = userService.rejectAdmin(userId);

            if (result.startsWith("✅")) {
                return ResponseEntity.ok(Map.of("message", result, "success", "true"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", result, "success", "false"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "success", "false"));
        }
    }
}