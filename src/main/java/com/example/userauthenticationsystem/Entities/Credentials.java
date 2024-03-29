package com.example.userauthenticationsystem.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class Credentials implements Serializable {
    @Id
    private int id; // Using the same primary key as User
    private String email;
    private String password;
    @OneToOne
    @MapsId
    private User user;
}
