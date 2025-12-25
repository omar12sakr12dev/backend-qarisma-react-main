package com.qarisma.api.config;

import com.qarisma.api.entity.User;
import com.qarisma.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@qarisma.com")
                    .password(passwordEncoder.encode("Admin@123!"))
                    .fullName("مدير النظام")
                    .role(User.Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Default admin user created: admin / Admin@123!");
        }
    }
}
