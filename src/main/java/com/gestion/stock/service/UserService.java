package com.gestion.stock.service;

import com.gestion.stock.entities.Erole;
import com.gestion.stock.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

   // User registerNewUserAccount(User userDto , Set<Erole> roles);
    User registerNewUserAccount(User userDto,Erole role);
    void sendActivationMail(User user);
    boolean activateUserAccount(String token);
    List<User> findAllUsers();
    Optional<User> findUserById(Long id);
    void updateRole(Long userId , Set<Erole> roles);
    void enableUserAccount(Long userId);
    void disableUserAccount(Long userId);
    User saveUser(User user);
    List<Erole> getAllRoles();









}
