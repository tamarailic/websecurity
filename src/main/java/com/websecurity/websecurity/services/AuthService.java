package com.websecurity.websecurity.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.websecurity.websecurity.DTO.UserDTO;
import com.websecurity.websecurity.exceptions.NonExistantUserException;
import com.websecurity.websecurity.exceptions.VerificationTokenExpiredException;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.repositories.IRoleRepository;
import com.websecurity.websecurity.repositories.IUserRepository;
import com.websecurity.websecurity.security.Role;
import com.websecurity.websecurity.security.jwt.JwtTokenUtil;
import com.websecurity.websecurity.services.email.EmailService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private EmailService emailService;
    @Value("${server.ip}")
    private String IP;
    @Value("${server.port}")
    private int PORT;
    @Autowired
    IHelperService helperService;


    private static final int USER_CREDENTIALS_EXPIRY_DAYS = 7;

    @Override
    public User registerUser(UserDTO dto) {
        User newUser = createNewUser(dto);

        newUser = userRepository.save(newUser);
        String verificationToken = jwtTokenUtil.generateVerificationToken(newUser.getUsername());

        if (dto.isEmailValidation())
            emailService.sendVerificationEmail(newUser, "http://localhost:" + PORT + "/api/auth/verify?token=" + verificationToken);
        else {
            Twilio.init(helperService.getTwilioSID(), helperService.getTwilioToken());
            Message.creator(new PhoneNumber(dto.getPhone()),
                    new PhoneNumber(helperService.getTwilioPhone()),"http://" + IP + ":"  + PORT + "/api/auth/verify?token=" + verificationToken ).create();
        }
        return newUser;
    }

    @Override
    public boolean verify(String verificationToken) throws VerificationTokenExpiredException, NonExistantUserException {
        Date expiration = jwtTokenUtil.getExpirationDateFromToken(verificationToken);
        if (expiration.before(new Date())) {
            throw new VerificationTokenExpiredException();
        }

        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(jwtTokenUtil.getUsernameFromToken(verificationToken)));
        if (user.isEmpty()) throw new NonExistantUserException();
        User actual = user.get();
        if (actual.isEnabled()) return false;
        actual.setEnabled(true);
        userRepository.save(actual);
        return true;
    }


    private User createNewUser(UserDTO userDTO) {

        User newUser = userDTO.toUser();
        newUser.setActive(false);
        newUser.setNonLocked(true);
        newUser.setEnabled(false);
        newUser.setCredentialsExpiry(LocalDateTime.now().plusDays(7));
        List<Role> passengerRole = roleRepository.findByName("user");
        newUser.setRoles(passengerRole);

        String encodedPassword = passwordEncoder.encode(userDTO.password);
        newUser.setPassword(encodedPassword);
        return newUser;
    }

    @Override
    public void setRoles() {
        roleRepository.save(new Role("1", "user"));
        roleRepository.save(new Role("2", "admin"));
    }
}
