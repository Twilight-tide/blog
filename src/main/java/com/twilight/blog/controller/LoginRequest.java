package com.twilight.blog.controller;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}