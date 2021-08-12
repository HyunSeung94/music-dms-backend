package com.mesim.sc.security;

import lombok.Data;

@Data
public class UserPrincipal {

    private String username;
    private String password;
    private String[] roles;

    public UserPrincipal() {}

    public UserPrincipal(String username, String password){
        this.username = username;
        this.password = password;
    }
    public UserPrincipal(String username, String password, String... roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

}
