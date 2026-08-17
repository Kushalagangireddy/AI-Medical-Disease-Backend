 package com.medical.service;

import com.medical.entity.User;

public interface AuthService {

    User registerUser(User user);

    User loginUser(String email, String password);

}