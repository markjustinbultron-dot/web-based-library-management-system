package com.library.config;

import com.library.model.Role;
import com.library.model.User;
import com.library.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create initial Admin account if database is empty
        if (userRepository.findByEmail("admin@library.com").isEmpty()) {
            User admin = new User(
                "System Admin",
                "admin@library.com",
                "ADMIN001",
                "0000000000", // Phone number added here to match constructor
                passwordEncoder.encode(
                	    System.getenv().getOrDefault("ADMIN_INITIAL_PASSWORD", "admin123")
                	),
                Role.ADMIN
            );
            userRepository.save(admin);
        }
    }
}