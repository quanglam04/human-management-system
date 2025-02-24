package com.vti.lab7.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private int page = 1;  
    private int size = 10; 
    private long userId;
    private String username;
    private String email;
    private String password;
    private String sortBy = "id"; 
    private String sortDirection = "asc"; 
    
}