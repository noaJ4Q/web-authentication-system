package com.example.userauthenticationsystem.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import java.io.Serializable;

@Entity
public class Credentials implements Serializable {
    @Id
    private int id;
    @OneToOne
    @JoinColumn(name = "user")
    private User user;
    private String email;
    private String password;
}
