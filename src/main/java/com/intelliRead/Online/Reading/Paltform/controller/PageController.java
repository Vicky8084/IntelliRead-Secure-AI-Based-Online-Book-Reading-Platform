package com.intelliRead.Online.Reading.Paltform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // ✅ Home Page
    @GetMapping("/")
    public String home() {
        return "Home";
    }

    @GetMapping("/Home")
    public String homePage() {
        return "Home";
    }

    // ✅ Auth Pages
    @GetMapping("/login")
    public String loginPage() {
        return "Login";
    }

    @GetMapping("/Login")
    public String loginPage2() {
        return "Login";
    }

    @GetMapping("/signup")
    public String registerPage() {
        return "SignUp";
    }

    @GetMapping("/SignUp")
    public String registerPage2() {
        return "SignUp";
    }

    @GetMapping("/forgotpassword")
    public String forgotPasswordPage() {
        return "ForgotPass";
    }

    @GetMapping("/ForgotPass")
    public String forgotPasswordPage2() {
        return "ForgotPass";
    }

    // ✅ Admin Page
    @GetMapping("/admin")
    public String adminPage() {
        return "Admin";
    }

    @GetMapping("/Admin")
    public String adminPage2() {
        return "Admin";
    }

    // ✅ Additional pages
    @GetMapping("/books")
    public String books() {
        return "books";
    }

    @GetMapping("/bookscreen")
    public String bookScreen() {
        return "bookscreen";
    }

    @GetMapping("/publisher-dashboard")
    public String publisherDashboard() {
        return "publisher-dashboard";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    // ✅ NEW: Publisher Route Add Karo (Aapke login code ke liye)
    @GetMapping("/publisher")
    public String publisher() {
        return "publisher-dashboard"; // Same page as publisher-dashboard
    }
}