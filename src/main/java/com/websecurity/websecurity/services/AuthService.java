package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.UserDTO;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.repositories.IRoleRepository;
import com.websecurity.websecurity.repositories.IUserRepository;
import com.websecurity.websecurity.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService implements IAuthService {
    private static final int USER_CREDENTIALS_EXPIRY_DAYS = 7;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IUserRepository userRepository;

    @Override
    public User registerUser(UserDTO dto) {
        User newPassenger = createNewUser(dto);

        newPassenger = userRepository.save(newPassenger);


        return newPassenger;
    }


    private User createNewUser(UserDTO userDTO) {

        User newUser = userDTO.toUser();
        newUser.setActive(false);
        newUser.setNonLocked(true);
        newUser.setEnabled(true);
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
