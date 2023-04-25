package com.websecurity.websecurity.controllers;

import com.websecurity.websecurity.DTO.CredentialsDTO;
import com.websecurity.websecurity.DTO.TokenDTO;
import com.websecurity.websecurity.DTO.UserDTO;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.repositories.IUserRepository;
import com.websecurity.websecurity.security.jwt.JwtTokenUtil;
import com.websecurity.websecurity.services.IAuthService;
import com.websecurity.websecurity.validators.LoginValidator;
import com.websecurity.websecurity.validators.LoginValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    IUserRepository userRepository;
    @Autowired
    IAuthService authService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PermitAll
    @PostMapping("/register")
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
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        User userWithThisEmail = userRepository.findByUsername(dto.getUsername());
        if (userWithThisEmail != null) {
            return new ResponseEntity<>("User with that email already exists!", HttpStatus.BAD_REQUEST);
        }

        try {
            authService.registerUser(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dto.password = "";
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PermitAll
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody CredentialsDTO credentialsDTO) {
        try {
            LoginValidator.validateRequired(credentialsDTO.getEmail(), "email");
            LoginValidator.validateRequired(credentialsDTO.getPassword(), "password");

            LoginValidator.validateEmail(credentialsDTO.getEmail(), "email");
        } catch (LoginValidatorException e1) {
            return new ResponseEntity<>(e1.getMessage(), HttpStatus.BAD_REQUEST);
        }

        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(credentialsDTO.getEmail(), credentialsDTO.getPassword());

        Authentication auth = null;
        try {
            auth = authenticationManager.authenticate(authReq);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Wrong username or password!", HttpStatus.BAD_REQUEST);
        } catch (DisabledException e) {
            return new ResponseEntity<>("User is disabled!", HttpStatus.BAD_REQUEST);
        }

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        String id = ((User) auth.getPrincipal()).getId();
        String token = jwtTokenUtil.generateToken(id, credentialsDTO.getEmail(), auth.getAuthorities());
        String refreshToken = jwtTokenUtil.generateRefreshToken(id, credentialsDTO.getEmail());
        TokenDTO tokens = new TokenDTO(token, refreshToken);

        User user = userRepository.findByUsername(credentialsDTO.getEmail());

        return new ResponseEntity<TokenDTO>(tokens, HttpStatus.OK);
    }


    @PermitAll
    @GetMapping
    public ResponseEntity<?> create() {
        authService.setRoles();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
