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
        System.out.println("🚀 Starting IntelliRead Application...");

        // 1️⃣ Create default admin accounts
        createDefaultAdmins();

        // 2️⃣ Auto-encode all existing users whose passwords are still plain text
        userRepository.findAll().forEach(user -> {
            String password = user.getPasswordHash();
            if (password != null && !password.startsWith("$2a$")) {
                user.setPasswordHash(passwordEncoder.encode(password));
                userRepository.save(user);
                System.out.println("🔒 Password encoded for user: " + user.getEmail());
            }
        });

        System.out.println("✅ Application startup completed.");
    }

    private void createDefaultAdmins() {
        System.out.println("🛠️ Creating default admin accounts...");

        List<User> defaultAdmins = Arrays.asList(
                createAdminUser("noreply.intelliread@gmail.com", "Original Admin", "vicky"),
                createAdminUser("admin1.intelliread@gmail.com", "Admin One", "admin123"),
                createAdminUser("admin2.intelliread@gmail.com", "Admin Two", "admin123"),
                createAdminUser("admin3.intelliread@gmail.com", "Admin Three", "admin123"),
                createAdminUser("admin4.intelliread@gmail.com", "Admin Four", "admin123")
        );

        int createdCount = 0;
        int existingCount = 0;

        for (User admin : defaultAdmins) {
            if (userRepository.findUserByEmail(admin.getEmail()).isEmpty()) {
                userRepository.save(admin);
                createdCount++;
                System.out.println("✅ Default admin CREATED: " + admin.getEmail());
                System.out.println("   👤 Name: " + admin.getName());
                System.out.println("   🔑 Role: " + admin.getRole());
                System.out.println("   📊 Status: " + admin.getStatus());
                System.out.println("   🔐 Password: " + getRawPasswordForEmail(admin.getEmail()));
            } else {
                existingCount++;
                System.out.println("ℹ️ Default admin ALREADY EXISTS: " + admin.getEmail());

                // Update existing admin to ensure correct role and status
                User existingAdmin = userRepository.findUserByEmail(admin.getEmail()).get();
                existingAdmin.setRole(Role.ROLE);
                existingAdmin.setStatus(Status.ACTIVE);
                userRepository.save(existingAdmin);
                System.out.println("   🔄 Updated existing admin role to ROLE and status to ACTIVE");
            }
        }

        System.out.println("📊 Admin Creation Summary:");
        System.out.println("   ✅ Created: " + createdCount);
        System.out.println("   ℹ️ Existing: " + existingCount);
        System.out.println("   📝 Total: " + defaultAdmins.size());

        // Print login instructions
        System.out.println("\n🔐 ADMIN LOGIN CREDENTIALS:");
        System.out.println("   📧 noreply.intelliread@gmail.com | 🔑 vicky");
        System.out.println("   📧 admin1.intelliread@gmail.com  | 🔑 admin123");
        System.out.println("   📧 admin2.intelliread@gmail.com  | 🔑 admin123");
        System.out.println("   📧 admin3.intelliread@gmail.com  | 🔑 admin123");
        System.out.println("   📧 admin4.intelliread@gmail.com  | 🔑 admin123");
        System.out.println("\n💡 Login as PUBLISHER role with these credentials");
    }

    private User createAdminUser(String email, String name, String password) {
        User admin = new User();
        admin.setName(name);
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(password));

        // ✅ Use ROLE as the role for admin accounts (since that's what's available)
        admin.setRole(Role.ROLE);
        admin.setStatus(Status.ACTIVE);
        admin.setPreferredLanguage("English");
        return admin;
    }

    private String getRawPasswordForEmail(String email) {
        switch(email) {
            case "noreply.intelliread@gmail.com": return "vicky";
            case "admin1.intelliread@gmail.com": return "admin123";
            case "admin2.intelliread@gmail.com": return "admin123";
            case "admin3.intelliread@gmail.com": return "admin123";
            case "admin4.intelliread@gmail.com": return "admin123";
            default: return "unknown";
        }
    }
}