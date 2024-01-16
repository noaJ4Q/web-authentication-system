package com.example.userauthenticationsystem.Repositories;

import com.example.userauthenticationsystem.Entities.Token;
import com.example.userauthenticationsystem.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Token findByCode(String code);
    Token findByUser(User user);
}