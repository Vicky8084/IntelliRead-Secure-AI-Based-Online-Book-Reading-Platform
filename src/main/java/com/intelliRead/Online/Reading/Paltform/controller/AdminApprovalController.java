//package com.intelliRead.Online.Reading.Paltform.controller;
//
//import com.intelliRead.Online.Reading.Paltform.enums.Status;
//import com.intelliRead.Online.Reading.Paltform.model.User;
//import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
//import com.intelliRead.Online.Reading.Paltform.service.EmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/admin")
//public class AdminApprovalController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private EmailService emailService;
//
//    @GetMapping("/approve/{userId}")
//    public String approveAdmin(@PathVariable int userId) {
//        User user = userRepository.findById(userId).orElseThrow();
//        user.setStatus(Status.ACTIVE);
//        userRepository.save(user);
//        emailService.sendAdminApproved(user);
//        return "✅ Admin approved successfully!";
//    }
//
//    @GetMapping("/reject/{userId}")
//    public String rejectAdmin(@PathVariable int userId) {
//        User user = userRepository.findById(userId).orElseThrow();
//        userRepository.delete(user);
//        emailService.sendAdminRejected(user);
//        return "❌ Admin registration rejected!";
//    }
//}
