package com.websecurity.websecurity.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.websecurity.websecurity.DTO.PreviousPasswordDTO;
import com.websecurity.websecurity.DTO.UserDTO;
import com.websecurity.websecurity.exceptions.NonExistantUserException;
import com.websecurity.websecurity.exceptions.VerificationTokenExpiredException;
import com.websecurity.websecurity.models.PasswordChangeRequest;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.repositories.IPasswordChangeRequestRepository;
import com.websecurity.websecurity.repositories.IRoleRepository;
import com.websecurity.websecurity.repositories.IUserRepository;
import com.websecurity.websecurity.security.Role;
import com.websecurity.websecurity.security.jwt.JwtTokenUtil;
import com.websecurity.websecurity.services.email.EmailService;
import com.websecurity.websecurity.validators.LoginValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService implements IAuthService {
    private static final int USER_CREDENTIALS_EXPIRY_DAYS = 7;
    private static final int UPPER_BOUND = 10000000;

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
    @Autowired
    IPasswordChangeRequestRepository passwordChangeRequestRepository;


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
                    new PhoneNumber(helperService.getTwilioPhone()), "http://" + IP + ":" + PORT + "/api/auth/verify?token=" + verificationToken).create();
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

    @Override
    public void generatePasswordChangeRequest(User user) {
        boolean emailValidation = user.getEmailValidation();
        String code = Integer.toString((new Random()).nextInt(UPPER_BOUND));
        passwordChangeRequestRepository.save(new PasswordChangeRequest(user, passwordEncoder.encode(code)));

        if (emailValidation) {
            emailService.sendPasswordChangeEmail(user, code);
        } else {
            Twilio.init(helperService.getTwilioSID(), helperService.getTwilioToken());
            Message.creator(new PhoneNumber(user.getPhone()),
                    new PhoneNumber(helperService.getTwilioPhone()), "Your verification code is:" + code).create();
        }
    }

    @Override
    public void setNewUserPassword(User user, PreviousPasswordDTO dto) throws LoginValidatorException {
        user.addPreviousPassword();
        for (String password :
                user.getPreviousPasswords()) {
            if (passwordEncoder.matches(dto.password, password))
                throw new LoginValidatorException("Password matches one of the previous ones!");
        }
        user.setPassword(passwordEncoder.encode(dto.password));
        user.setCredentialsExpiry(LocalDateTime.now().plusDays(USER_CREDENTIALS_EXPIRY_DAYS));
        userRepository.save(user);

    }
}
