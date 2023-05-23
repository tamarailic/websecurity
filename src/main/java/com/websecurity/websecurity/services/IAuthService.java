package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.PreviousPasswordDTO;
import com.websecurity.websecurity.DTO.UserDTO;
import com.websecurity.websecurity.exceptions.NonExistantUserException;
import com.websecurity.websecurity.exceptions.VerificationTokenExpiredException;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.validators.LoginValidatorException;


public interface IAuthService {
    User registerUser(UserDTO dto);

    void setRoles();
    boolean verify(String verificationToken) throws VerificationTokenExpiredException, NonExistantUserException;

    void generatePasswordChangeRequest(User user);

    void setNewUserPassword(User user, PreviousPasswordDTO dto) throws LoginValidatorException;
}
