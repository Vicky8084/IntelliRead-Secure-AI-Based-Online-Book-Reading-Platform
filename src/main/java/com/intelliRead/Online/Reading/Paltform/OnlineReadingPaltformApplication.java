package com.intelliRead.Online.Reading.Paltform;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.enums.Status;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class OnlineReadingPaltformApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(OnlineReadingPaltformApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        // 1️⃣ Create default admin accounts
        createDefaultAdmins();

        // 2️⃣ Auto-encode all existing users whose passwords are still plain text
        userRepository.findAll().forEach(user -> {
            String password = user.getPasswordHash();
            if (password != null && !password.startsWith("$2a$")) {
                user.setPasswordHash(passwordEncoder.encode(password));
                userRepository.save(user);
            }
        });

    }

    private void createDefaultAdmins() {

        List<User> defaultAdmins = Arrays.asList(
                createAdminUser("noreply.intelliread@gmail.com", "Original Admin", "Password098@"),
                createAdminUser("mrvg4545@gmail.com", "Admin One", "Password098@"),
                createAdminUser("aaarti.rcc090@gmail.com", "Admin Two", "Password098@"),
                createAdminUser("jarpit0103@gmail.com", "Admin Three", "Password098@"),
                createAdminUser("rwi.sharma001@gmail.com", "Admin Four", "Password098@")
        );

        int createdCount = 0;
        int existingCount = 0;

        for (User admin : defaultAdmins) {
            if (userRepository.findUserByEmail(admin.getEmail()).isEmpty()) {
                userRepository.save(admin);
                createdCount++;
            } else {
                existingCount++;

                // Update existing admin to ensure correct role and status
                User existingAdmin = userRepository.findUserByEmail(admin.getEmail()).get();
                existingAdmin.setRole(Role.ADMIN);
                existingAdmin.setStatus(Status.ACTIVE);
                userRepository.save(existingAdmin);
            }
        }

    }

    private User createAdminUser(String email, String name, String password) {
        User admin = new User();
        admin.setName(name);
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(password));

        // ✅ Use ROLE as the role for admin accounts (since that's what's available)
        admin.setRole(Role.ADMIN);
        admin.setStatus(Status.ACTIVE);
        admin.setPreferredLanguage("English");
        return admin;
    }

    private String getRawPasswordForEmail(String email) {
        switch(email) {
            case "noreply.intelliread@gmail.com": return "Password098@";
            case "mrvg4545@gmail.com": return "Password098@";
            case "aaarti.rcc090@gmail.com": return "Password098@";
            case "jarpit0103@gmail.com": return "Password098@";
            case "rwi.sharma001@gmail.com": return "Password098@";
            default: return "unknown";
        }
    }
}