package com.example.demo.service;

import com.example.demo.entity.User;

import java.util.List;
import java.util.Map;


public interface UserService {
    User getUserById(int userId);

    void addUser(User record) throws Exception;

    List<User> getAll();


}
