package com.vti.lab7.dto.response;

import com.vti.lab7.model.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private long userId;
    private String username;
    private String email;
    private String roleName; 

    public UserResponse(long userId, String username, String email, Role role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roleName = role.getRoleName();
    }
}
