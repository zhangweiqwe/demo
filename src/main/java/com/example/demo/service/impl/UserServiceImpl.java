package com.example.demo.service.impl;

import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;



    @Override
    public User getUserById(int userId) {
        return userDao.selectByPrimaryKey(userId);
    }

    @Override
    public void addUser(User record) {
        userDao.insertSelective(record);
    }


    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }
}
