package com.app.dto;

import com.app.model.User;

public class UserDTO {
    private Integer id;
    private String username;
    private String role;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername()  {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserDTO() {};

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
    }
}
