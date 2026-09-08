package com.letsplay.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.letsplay.api.model.Role;
import com.letsplay.api.model.User;
import com.letsplay.api.repository.UserRepository;

/**
 * AdminInitializer
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Autowired 
    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean adminExists = userRepository.existsByRole(Role.ADMIN);
        if (!adminExists) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            System.out.println("       ");
            String RESET = "\u001B[0m";
            String BOLD_YELLOW = "\u001B[1;33m";
            String BOLD_GREEN = "\u001B[1;32m";
            String BOLD_CYAN = "\u001B[1;36m";

            System.out.println();
            System.out.println(BOLD_CYAN
                    + "╔══════════════════════════════════════════════════════════════════════════════╗" + RESET);
            System.out.println(BOLD_CYAN
                    + "║                                                                              ║" + RESET);
            System.out.println(BOLD_CYAN + "║  " + BOLD_YELLOW + "► SYSTEM INITIALIZATION NOTICE:" + RESET
                    + "                                             " + BOLD_CYAN + "║" + RESET);
            System.out.println(BOLD_CYAN
                    + "║     No admin account detected in database.                                   ║" + RESET);
            System.out.println(BOLD_CYAN + "║     Created default admin: " + BOLD_GREEN
                    + String.format("%-42s", adminEmail) + RESET + BOLD_CYAN + " ║" + RESET);
            System.out.println(BOLD_CYAN
                    + "║                                                                              ║" + RESET);
            System.out.println(BOLD_CYAN
                    + "╚══════════════════════════════════════════════════════════════════════════════╝" + RESET);
            System.out.println();
        }
    }
}
