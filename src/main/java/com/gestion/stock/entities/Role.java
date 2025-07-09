package com.gestion.stock.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,name = "rolename")
    @Enumerated(EnumType.STRING)
    private Erole roleName;


    public Role(Erole roleName) {
        this.roleName = roleName;
    }
}
