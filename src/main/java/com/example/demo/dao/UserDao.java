package com.example.demo.dao;


import com.example.demo.entity.User;

import java.util.List;

public interface UserDao {
    int insert(User user);

    int delete(String id);

    int update(User user);

    User query(String id);

    List<User> getAll();


}