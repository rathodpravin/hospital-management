package com.hospital.config;
import com.hospital.entity.*;
import com.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
/** Creates the first ADMIN account if none exists. */
@Component @RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    @Value("${app.admin.username}") private String adminUser;
    @Value("${app.admin.password}") private String adminPass;
    @Override public void run(String... args) {
        if (!users.existsByUsername(adminUser)) {
            User u = new User();
            u.setUsername(adminUser); u.setPassword(encoder.encode(adminPass)); u.setRole(Role.ADMIN);
            users.save(u);
        }
    }
}
