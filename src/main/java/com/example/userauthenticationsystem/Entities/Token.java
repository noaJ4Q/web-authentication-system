package com.example.userauthenticationsystem.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Token implements Serializable {
    @Id
    private int id;
    private String code;
    private LocalDateTime expirityDate;

    @OneToOne
    @MapsId
    private User user;
}
