package com.gestion.stock.config;

import com.gestion.stock.entities.Erole;
import com.gestion.stock.entities.Role;
import com.gestion.stock.entities.User;
import com.gestion.stock.repository.RoleRepository;
import com.gestion.stock.repository.UserRepository;
import com.gestion.stock.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitConfig {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    //@Bean
    CommandLineRunner initData() {
        return args -> {

            //1-Creation des Roles
            Role superAdminRole = new Role(Erole.Role_SUPERADMIN);
           Role adminRole = new Role(Erole.ROLE_ADMIN);
           Role userRole = new Role(Erole.ROLE_USER);

            //Role adminRole  = new Role();
            //Role userRole  = new Role();
            //adminRole.setRoleName(Erole.ROLE_ADMIN);
           // userRole.setRoleName(Erole.ROLE_USER);
            roleRepository.save(superAdminRole);
            roleRepository.save(adminRole);
            roleRepository.save(userRole);

            //2-Creation des Utilisateurs
           User superAdmin = User.builder()
                   .userName("Aymen")
                   .email("aymen@gmail.com")
                   .password(passwordEncoder.encode("123456"))
                   .enabled(true)
                   .roles(Set.of(superAdminRole, adminRole, userRole))
                   .build();
            User admin = User.builder()
                    .userName("admin")
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("123456"))
                    .enabled(true)
                    .roles(Set.of(adminRole, userRole))
                    .build();
            User user = User.builder()
                    .userName("user")
                    .email("user@user.com")
                    .password(passwordEncoder.encode("123456"))
                    .enabled(true)
                    .roles(Set.of(userRole))
                    .build();
            userRepository.save(superAdmin);
            userRepository.save(admin);
            userRepository.save(user);
            System.out.println( "Super Admin" + superAdmin + "Admin: " + admin + " user: " + user + "a etait enregistre avec succes");

        };
    }








}
