package com.example.userauthenticationsystem.Repositories;

import com.example.userauthenticationsystem.Entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Token findByCode(String code);
}