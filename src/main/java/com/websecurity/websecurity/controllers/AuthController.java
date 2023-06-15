package com.websecurity.websecurity.controllers;

import com.websecurity.websecurity.DTO.*;
import com.websecurity.websecurity.exceptions.NonExistantUserException;
import com.websecurity.websecurity.exceptions.VerificationTokenExpiredException;
import com.websecurity.websecurity.models.LoginAttempt;
import com.websecurity.websecurity.models.PasswordChangeRequest;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.repositories.ILoginAttemptRepository;
import com.websecurity.websecurity.repositories.IPasswordChangeRequestRepository;
import com.websecurity.websecurity.repositories.IUserRepository;
import com.websecurity.websecurity.security.jwt.JwtTokenUtil;
import com.websecurity.websecurity.services.IAuthService;
import com.websecurity.websecurity.services.email.EmailService;
import com.websecurity.websecurity.validators.LoginValidator;
import com.websecurity.websecurity.validators.LoginValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.websocket.server.PathParam;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

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
    @Autowired
    EmailService emailService;
    @Autowired
    IPasswordChangeRequestRepository passwordChangeRequestRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ILoginAttemptRepository loginAttemptRepository;


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

            LoginValidator.validatePattern(dto.getPassword(), "password", "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
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
        try {
            authService.create2FA(credentialsDTO, auth);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);

    }

    @PermitAll
    @PostMapping("/2fa")
    public ResponseEntity<?> factorAuth(@RequestBody CodeDTO codeDTO) {
        try {
            LoginValidator.validateRequired(codeDTO.getEmail(), "email");
            LoginValidator.validateRequired(codeDTO.getPassword(), "password");
            LoginValidator.validateEmail(codeDTO.getEmail(), "email");
            LoginAttempt attempt = loginAttemptRepository.findByUserEmail(codeDTO.getEmail());
            if (passwordEncoder.matches(codeDTO.getCode(), attempt.getCode()))
                loginAttemptRepository.delete(attempt);
            else
                throw new Exception("Something went wrong");
        } catch (Exception e1) {
            return new ResponseEntity<>(e1.getMessage(), HttpStatus.BAD_REQUEST);
        }

        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(codeDTO.getEmail(), codeDTO.getPassword());

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
        String token = jwtTokenUtil.generateToken(id, codeDTO.getEmail(), auth.getAuthorities());
        String refreshToken = jwtTokenUtil.generateRefreshToken(id, codeDTO.getEmail());
        TokenDTO tokens = new TokenDTO(token, refreshToken);

        User user = userRepository.findByUsername(codeDTO.getEmail());

        return new ResponseEntity<TokenDTO>(tokens, HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String code) {
        boolean verified = false;
        try {
            verified = authService.verify(code);
        } catch (VerificationTokenExpiredException e) {
            return new ResponseEntity<>("Activation expired. Register again!", HttpStatus.BAD_REQUEST);
        } catch (NonExistantUserException e) {
            return new ResponseEntity<>("Activation with entered id does not exist!", HttpStatus.NOT_FOUND);
        }

        URI yahoo = null;
        HttpHeaders httpHeaders = new HttpHeaders();
        if (verified) {

            try {
                yahoo = new URI("http://localhost:3000/login");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            httpHeaders.setLocation(yahoo);
            return new ResponseEntity<>(httpHeaders, HttpStatus.OK);

        } else {

            try {
                yahoo = new URI("http://localhost:4200/bad-request");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            httpHeaders.setLocation(yahoo);
            return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PermitAll
    @PostMapping(value = "/refreshToken")
    public ResponseEntity<TokenDTO> refreshtoken(@RequestBody String refreshToken) throws Exception {
        // From the HttpRequest get the claims
        refreshToken = refreshToken.replace("\"", "");
        refreshToken = refreshToken.replace("{", "");
        refreshToken = refreshToken.replace("}", "");
        refreshToken = refreshToken.replace("\\", "");
        refreshToken = refreshToken.split(":")[1];
        String email = jwtTokenUtil.getEmailFromToken(refreshToken);
        User user = userRepository.findByUsername(email);

        if (jwtTokenUtil.validateToken(refreshToken, user)) {
            String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getAuthorities());
            System.out.println("Refreshed token");
            TokenDTO tokenDTO = new TokenDTO(token, refreshToken);
            return new ResponseEntity<>(tokenDTO, HttpStatus.OK);

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PermitAll
    @GetMapping("/change")
    public ResponseEntity<?> changePassword(@PathParam("username") String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        authService.generatePasswordChangeRequest(user);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PermitAll
    @PostMapping("/change")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordChangeDTO dto) {
        List<PasswordChangeRequest> requests = passwordChangeRequestRepository.findAll();
        PasswordChangeRequest currentRequest = null;
        for (PasswordChangeRequest request :
                requests) {
            if (passwordEncoder.matches(dto.getCode(), request.getCode())) {
                currentRequest = request;
                break;
            }
        }
        if (currentRequest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if (passwordEncoder.matches(dto.getPassword(), currentRequest.getUser().getPassword()))
            return new ResponseEntity<>("Password must be different then the old one", HttpStatus.BAD_REQUEST);
        currentRequest.getUser().setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(currentRequest.getUser());
        passwordChangeRequestRepository.delete(currentRequest);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PermitAll
    @PostMapping("/refresh-password")
    public ResponseEntity<?> refreshPassword(@RequestBody PreviousPasswordDTO dto) {
        try {

            LoginValidator.validateRequired(dto.username, "username");
            LoginValidator.validateRequired(dto.previousPassword, "previousPassword");
            LoginValidator.validateRequired(dto.password, "password");

        } catch (LoginValidatorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByUsername(dto.username);
        if (user == null) return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        if (!user.isCredentialsNonExpired()) {
            if (passwordEncoder.matches(dto.previousPassword, user.getPassword())) {
                try {
                    LoginValidator.validatePattern(dto.password, "password", "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
                    authService.setNewUserPassword(user, dto);
                    return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
                } catch (LoginValidatorException e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                }
            }
        }
        return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);

    }

    @PermitAll
    @GetMapping
    public ResponseEntity<?> create() {
        authService.setRoles();
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
