package com.websecurity.websecurity.controllers;

import com.websecurity.websecurity.DTO.UserDTO;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.repositories.IUserRepository;
import com.websecurity.websecurity.services.IAuthService;
import com.websecurity.websecurity.validators.LoginValidator;
import com.websecurity.websecurity.validators.LoginValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.io.IOException;

@RestController
@RequestMapping("/api/register")
public class AuthController {

    @Autowired
    IUserRepository userRepository;
    @Autowired
    IAuthService authService;

    @PermitAll
    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserDTO dto) {
        try {
            LoginValidator.validateRequired(dto.getName(), "name");
            LoginValidator.validateRequired(dto.getSurname(), "surname");
            LoginValidator.validateRequired(dto.getUsername(), "email");
            LoginValidator.validateRequired(dto.getPassword(), "password");

            LoginValidator.validateLength(dto.getName(), "name", 100);
            LoginValidator.validateLength(dto.getSurname(), "surname", 100);
            LoginValidator.validateLength(dto.getUsername(), "email", 100);

            LoginValidator.validatePattern(dto.getPassword(), "password", "^(?=.*\\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$");
        } catch (LoginValidatorException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        User userWithThisEmail = userRepository.findByUsername(dto.getUsername());
        if (userWithThisEmail != null) {
            return new ResponseEntity<>("User with that email already exists!", HttpStatus.BAD_REQUEST);
        }

        try {
            authService.registerUser(dto);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        dto.password = "";
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<?> create() {
        authService.setRoles();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
