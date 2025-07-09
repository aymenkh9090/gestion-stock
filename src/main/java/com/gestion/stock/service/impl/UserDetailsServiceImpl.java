package com.gestion.stock.service.impl;

import com.gestion.stock.entities.User;
import com.gestion.stock.repository.UserRepository;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;



    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //1--- Chercher l'utilisateur avec son user name
       User user =
                    userRepository.findByUserName(username)
                            .orElseThrow(()->new UsernameNotFoundException("User :" + username + "n'existe pas !!"));

       //2--- Convertir les roles de l'utilisateur en autorite(Granted autority) spring security
        Set<GrantedAuthority> authorities =
                    user.getRoles().stream()
                            .map(role->new SimpleGrantedAuthority(role.getRoleName().name()))
                            .collect(Collectors.toSet());

       //3---Cree un objet UserDetails avec les informations de l'utilisateur
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                authorities
        );
    }
}
