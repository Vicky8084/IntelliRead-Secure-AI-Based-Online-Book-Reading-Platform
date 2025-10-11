package com.intelliRead.Online.Reading.Paltform;

import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.enums.Status;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class OnlineReadingPaltformApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;


    public static void main(String[] args) {
        SpringApplication.run(OnlineReadingPaltformApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Default admin details
        String defaultEmail = "noreply.intelliread@gmail.com";
        if (userRepository.findUserByEmail(defaultEmail).isEmpty()) {

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); // password encoder

            User admin = new User();
            admin.setName("Original Admin");
            admin.setEmail(defaultEmail);
            admin.setPasswordHash(encoder.encode("vicky")); // secure hashed password
            admin.setRole(Role.ADMIN);
            admin.setStatus(Status.ACTIVE); // Admin is ACTIVE by default
            admin.setPreferredLanguage("English");

            userRepository.save(admin);
            System.out.println("✅ Default admin created: " + defaultEmail);
        } else {
            System.out.println("✅ Default admin already exists.");
        }
    }
}
