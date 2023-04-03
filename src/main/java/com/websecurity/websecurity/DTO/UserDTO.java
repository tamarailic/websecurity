package com.websecurity.websecurity.DTO;

import com.websecurity.websecurity.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    public String name;
    public String surname;
    public String username;
    public String password;

    public User toUser(){
        return new User(name,surname,username,password);
    }

}

