//package com.intelliRead.Online.Reading.Paltform.controller;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//public class HealthController {
//
//    @GetMapping("/health")
//    public Map<String, Object> healthCheck() {
//        Map<String, Object> healthInfo = new HashMap<>();
//        healthInfo.put("status", "✅ Online-Reading-Platform Server is Running!");
//        healthInfo.put("timestamp", LocalDateTime.now().toString());
//        healthInfo.put("port", 8035);
//        healthInfo.put("application", "Intelli-Read");
//        return healthInfo;
//    }
//
//    @GetMapping("/test-cors")
//    public String testCors() {
//        return "✅ CORS is working perfectly on port 8035!";
//    }
//}