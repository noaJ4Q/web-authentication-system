package com.example.userauthenticationsystem.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Credentials {
    @Id
    private int id;
    @OneToOne
    @JoinColumn(name = "user")
    private User user;
    private String email;
    private String password;
}
