package com.example.userauthenticationsystem.Services;

import com.example.userauthenticationsystem.Entities.Provider;
import com.example.userauthenticationsystem.Entities.User;
import com.example.userauthenticationsystem.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void processOAuthPostLogin(String email, String name){

        Optional<User> existUser = userRepository.findByEmail(email);

        if (existUser.isEmpty()){
            User newUser = new User();

            newUser.setFullname(name);
            newUser.setEmail(email);
            newUser.setState(1);
            newUser.setRole(0);
            newUser.setProvider(Provider.GOOGLE);

            userRepository.save(newUser);
        }
    }
}
