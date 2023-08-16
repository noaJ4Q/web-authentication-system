package com.example.userauthenticationsystem.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class userEntity {
    @Id
    private int id;
    private String fullname;
    private String email;
}
