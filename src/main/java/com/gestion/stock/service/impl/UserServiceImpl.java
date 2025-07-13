package com.gestion.stock.service.impl;

import com.gestion.stock.entities.Erole;
import com.gestion.stock.entities.Role;
import com.gestion.stock.entities.User;
import com.gestion.stock.repository.RoleRepository;
import com.gestion.stock.repository.UserRepository;
import com.gestion.stock.service.EmailService;
import com.gestion.stock.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JavaMailSender mailSender;
    private final EmailService emailService;


    @Override
    @Transactional
    public User registerNewUserAccount(User userDto,Erole role) {

        if(userRepository.existsByUserName(userDto.getUserName())) {
            throw new RuntimeException("Username already exists");
        }
        if(userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        Role userRole = roleRepository.findByRoleName(role)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        User user = new User();
        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEnabled(false); // par defaut compte desactivé
        user.setActivationToken(UUID.randomUUID().toString());

        User savedUser = userRepository.save(user);
        sendActivationMail(savedUser);
        return savedUser;
    }

    /*@Override
        @Transactional
        public User registerNewUserAccount(User userDto, Set<Erole> requestedRoles) {

            if (userRepository.existsByUserName(userDto.getUserName())) {
                throw new RuntimeException("Erreur : UserName déja existe!!");
            }
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new RuntimeException("Erreur : L'Email déja existe!!");
            }

            User user = new User();
            user.setUserName(userDto.getUserName());
            user.setEmail(userDto.getEmail());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setEnabled(false); // par defaut compte desactivé
            user.setActivationToken(UUID.randomUUID().toString());

            Set<Role> roles = new HashSet<>();
            if (requestedRoles == null || requestedRoles.isEmpty()) {
                Role defaultRole =
                        roleRepository.findByRoleName(Erole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("le role USER n'existe pas!!"));
                roles.add(defaultRole);
            } else
                requestedRoles.forEach(roleName -> {
                    Role role = roleRepository.findByRoleName(roleName)
                            .orElseThrow(() -> new RuntimeException("le role " + roleName + "n'existe pas!!"));
                    roles.add(role);
                });

            user.setRoles(roles);
            User savedUser = userRepository.save(user);
            sendActivationMail(savedUser);
            return savedUser;
        }
    */
    @Override
    public void sendActivationMail(User user) {

        String activationUrl = "http://localhost:8085/activate?token=" + user.getActivationToken();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("mail d'activation");
        message.setText("Pour activer votre compte..Clique sur ce lien au dessous : \n " + activationUrl);

        mailSender.send(message);
        System.out.println("Activation Email envoyé : pour  " + user.getEmail() + " avec le lien : " + activationUrl);


    }

    @Override
    @Transactional
    public boolean activateUserAccount(String token) {

        Optional<User> optionalUser =
                userRepository.findByActivationToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEnabled(true);
            user.setActivationToken(null); // on supprime le cle d'activation
            userRepository.save(user);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public void updateRole(Long userId,Erole newRoleName) {

       User user =
               userRepository.findById(userId)
                       .orElseThrow(() -> new RuntimeException("Utilisater"+userId+" n'existe pas!!! "));
       Role newRole =
               roleRepository.findByRoleName(newRoleName)
               .orElseThrow(() -> new RuntimeException("Role not found"));
       Set<Role> roles = new HashSet<>();
       roles.add(newRole);
       user.setRoles(roles);
       userRepository.save(user);
        try {
            emailService.sendActionNotification(user.getEmail(),"role_change",user.getUserName(),newRoleName.toString());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void enableUserAccount(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User avec Id:" + userId + "n'existe pas!!"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void disableUserAccount(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User avec Id:" + userId + "n'existe pas!!"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<Erole> getAllRoles() {
        return Arrays.asList(Erole.values());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Long countUsers() {
        return userRepository.count();
    }

    @Override
    public User updateUser(Long id, User updateUser, Erole role) {

        return
                userRepository.findById(id)
                        .map(user->{
                            user.setUserName(updateUser.getUserName());
                            user.setEmail(updateUser.getEmail());
                            Role rolename = roleRepository.findByRoleName(role).
                                    orElseThrow(()->new RuntimeException("Role not found"));
                            Set<Role> roles = new HashSet<>();
                            roles.add(rolename);
                            user.setRoles(roles);
                           return userRepository.save(user);
                        })
                .orElseThrow(()->new RuntimeException("User not found"));

    }
}
