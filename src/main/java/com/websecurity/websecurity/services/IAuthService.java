package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.CredentialsDTO;
import com.websecurity.websecurity.DTO.PreviousPasswordDTO;
import com.websecurity.websecurity.DTO.UserDTO;
import com.websecurity.websecurity.exceptions.NonExistantUserException;
import com.websecurity.websecurity.exceptions.VerificationTokenExpiredException;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.validators.LoginValidatorException;
import org.springframework.security.core.Authentication;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public interface IAuthService {
    User registerUser(UserDTO dto);

    void create2FA(CredentialsDTO credentialsDTO, Authentication auth) throws VerificationTokenExpiredException, NonExistantUserException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    void setRoles();

    boolean verify(String verificationToken) throws VerificationTokenExpiredException, NonExistantUserException;

    void generatePasswordChangeRequest(User user);

    User registerOauthUser(UserDTO dto);

    String generateCode();

    void setNewUserPassword(User user, PreviousPasswordDTO dto) throws LoginValidatorException;
}
