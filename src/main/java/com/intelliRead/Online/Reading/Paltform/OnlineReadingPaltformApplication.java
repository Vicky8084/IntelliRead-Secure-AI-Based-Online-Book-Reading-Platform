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

        // 1️⃣ Default admin creation
        String defaultEmail = "noreply.intelliread@gmail.com";
        if (userRepository.findUserByEmail(defaultEmail).isEmpty()) {

            User admin = new User();
            admin.setName("Original Admin");
            admin.setEmail(defaultEmail);
            admin.setPasswordHash(passwordEncoder.encode("vicky")); // Securely encoded password
            admin.setRole(Role.ROLE);
            admin.setStatus(Status.ACTIVE);
            admin.setPreferredLanguage("English");

            userRepository.save(admin);
            System.out.println("✅ Default admin created: " + defaultEmail);
        } else {
            System.out.println("✅ Default admin already exists.");
        }

        // 2️⃣ Auto-encode all existing users whose passwords are still plain text
        userRepository.findAll().forEach(user -> {
            String password = user.getPasswordHash();
            if (password != null && !password.startsWith("$2a$")) {
                user.setPasswordHash(passwordEncoder.encode(password));
                userRepository.save(user);
                System.out.println("🔒 Password encoded for user: " + user.getEmail());
            }
        });

        System.out.println("🚀 Password check and encoding completed.");
    }
}