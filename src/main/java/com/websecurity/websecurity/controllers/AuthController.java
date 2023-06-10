package com.websecurity.websecurity.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.websecurity.websecurity.DTO.*;
import com.websecurity.websecurity.exceptions.NonExistantUserException;
import com.websecurity.websecurity.exceptions.VerificationTokenExpiredException;
import com.websecurity.websecurity.models.PasswordChangeRequest;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.repositories.IPasswordChangeRequestRepository;
import com.websecurity.websecurity.repositories.IUserRepository;
import com.websecurity.websecurity.security.jwt.JwtTokenUtil;
import com.websecurity.websecurity.services.IAuthService;
import com.websecurity.websecurity.services.RecaptchaHelper;
import com.websecurity.websecurity.services.email.EmailService;
import com.websecurity.websecurity.validators.LoginValidator;
import com.websecurity.websecurity.validators.LoginValidatorException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
    RecaptchaHelper recaptchaHelper;
    @Autowired
    private HttpServletRequest request;


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

        try {
            if (!isRecaptchaValid(dto.getRecaptcha(), request.getRemoteAddr())) {
                return new ResponseEntity<>("Invalid captcha", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e2) {
            return new ResponseEntity<>(e2.getMessage(), HttpStatus.BAD_REQUEST);
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
        try {
            if (!isRecaptchaValid(credentialsDTO.getRecaptcha(), request.getRemoteAddr())) {
                return new ResponseEntity<>("Invalid captcha", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e2) {
            return new ResponseEntity<>(e2.getMessage(), HttpStatus.BAD_REQUEST);
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

        return new ResponseEntity<TokenDTO>(tokens, HttpStatus.OK);
    }

    private boolean isRecaptchaValid(String value, String clientIP) throws IOException {
        Request request = new Request.Builder()
                .url(String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s", recaptchaHelper.getSecret(), value, clientIP))
                .build();
        OkHttpClient client = new OkHttpClient();

        try (Response response = client.newCall(request).execute()) {
            String responseString = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            CaptchaResponseDTO captcha = objectMapper.readValue(responseString, CaptchaResponseDTO.class);
            return captcha.isSuccess();
        }
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
    public ResponseEntity<TokenDTO> refreshToken(@RequestBody String refreshToken) throws Exception {
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
    @GetMapping
    public ResponseEntity<?> createNew() {
        authService.setRoles();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PermitAll
    @PostMapping("/creteOrLogin")
    public ResponseEntity<?> oauth(@RequestBody OauthInfoDTO oauthInfoDTO) {
        User user = userRepository.findByUsername(oauthInfoDTO.getUsername());
        if (user != null) {
            String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getAuthorities());
            String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId(), user.getUsername());
            TokenDTO tokens = new TokenDTO(token, refreshToken);

            return new ResponseEntity<TokenDTO>(tokens, HttpStatus.OK);
        }

        try {
            user = authService.registerOauthUser(new UserDTO(oauthInfoDTO));
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid data", HttpStatus.BAD_REQUEST);
        }

        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getAuthorities());
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId(), user.getUsername());
        TokenDTO tokens = new TokenDTO(token, refreshToken);

        return new ResponseEntity<TokenDTO>(tokens, HttpStatus.OK);
    }
}
