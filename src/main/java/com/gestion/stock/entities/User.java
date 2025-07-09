package com.gestion.stock.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "user",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "username" ),
            @UniqueConstraint(columnNames = "email")
        })

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String userName;

    @Column(name = "email",nullable = false)
    @Email(message="Format de l'email est invalide")
    @NotEmpty(message = "Votre mail SVP !!")
    private String email;

    @Column(name = "password",nullable = false)
    @NotEmpty(message = "Tapez Votre mot de pass SVP !!")
    @Length(min = 5 , message = "Votre mot de pass doit contenir au moins 5 charachtere")
    private String password;

    private boolean enabled = false; // accounte enabled in dafault

    private String activationToken;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user-roles",
                joinColumns =
                @JoinColumn(name = "user_id"),
                inverseJoinColumns =
                @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;








}
