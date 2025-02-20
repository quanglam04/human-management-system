package com.vti.lab7.dto.response;

import com.vti.lab7.model.Role;
import com.vti.lab7.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private long userId;
    private String username;
    private String email;
    private String roleName; 

    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.roleName = user.getRole().getRoleName();
    }
}
