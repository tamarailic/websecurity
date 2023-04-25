package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.UserDTO;
import com.websecurity.websecurity.models.User;


public interface IAuthService {
    User registerUser(UserDTO dto);

    void setRoles();
}
