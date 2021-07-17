package io.cdhcs1516.ppmtool.service;

import io.cdhcs1516.ppmtool.domain.User;
import io.cdhcs1516.ppmtool.exceptions.UsernameException;
import io.cdhcs1516.ppmtool.repositories.UserRepository;
import io.cdhcs1516.ppmtool.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser(User newUser) {
        // Username should be unique (exception)
        try {
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            newUser.setUsername(newUser.getUsername());
            // Password should match with confirmPassword
            newUser.setConfirmPassword("");
            // we don't persist or show the confirmPassword
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new UsernameException("Username: '" + newUser.getUsername() + "' already exists");
        }


    }
}
