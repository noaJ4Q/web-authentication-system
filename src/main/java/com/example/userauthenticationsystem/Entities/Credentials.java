package com.example.userauthenticationsystem.Entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Credentials implements Serializable {
    @Id
    private int id;

    @OneToOne
    @MapsId
    private User user;

    private String email;
    private String password;
}
