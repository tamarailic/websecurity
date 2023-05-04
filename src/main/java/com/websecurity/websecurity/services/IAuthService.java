package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.UserDTO;
import com.websecurity.websecurity.exceptions.NonExistantUserException;
import com.websecurity.websecurity.exceptions.VerificationTokenExpiredException;
import com.websecurity.websecurity.models.User;


public interface IAuthService {
    User registerUser(UserDTO dto);

    void setRoles();
    boolean verify(String verificationToken) throws VerificationTokenExpiredException, NonExistantUserException;
}
