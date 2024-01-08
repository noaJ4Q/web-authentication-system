package com.example.userauthenticationsystem.Repositories;

import com.example.userauthenticationsystem.Entities.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, Integer> {
}