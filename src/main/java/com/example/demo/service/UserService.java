package com.example.demo.service;

import com.example.demo.entity.User;

import java.util.List;
import java.util.Map;


public interface UserService {
    int insert(User user);

    int delete(String id);

    int update(User user);

    User query(String id);

    List<User> getAll();


}
