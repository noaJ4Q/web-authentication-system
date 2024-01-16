package com.example.userauthenticationsystem.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Enter a valid full name")
    private String fullname;
    @NotBlank(message = "Enter a valid email")
    private String email;
    private int state;
    private int role;
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Credentials credentials;
}
