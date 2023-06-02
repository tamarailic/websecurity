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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
        String code = generateCode();
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
    public String generateCode() {
        return Integer.toString((new Random()).nextInt(UPPER_BOUND));
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

    @Override
    public byte[] encryptAuthentication(Authentication authentication, String secretKey) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] encryptedData = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(authentication);
        byte[] serializedData = bos.toByteArray();

        String encryptionAlgorithm = helperService.getEncryptAlgorithm();
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        encryptedData = cipher.doFinal(serializedData);


        return encryptedData;
    }

    @Override
    public Authentication dencryptAuthentication(byte[] encryptedData, String secretKey) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
        Authentication decryptedObject = null;

        String encryptionAlgorithm = helperService.getEncryptAlgorithm();
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);

        byte[] decryptedData = cipher.doFinal(encryptedData);

        ByteArrayInputStream bis = new ByteArrayInputStream(decryptedData);
        ObjectInputStream ois = new ObjectInputStream(bis);
        decryptedObject = (UsernamePasswordAuthenticationToken) ois.readObject();

        return decryptedObject;
    }
}
