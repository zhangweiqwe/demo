package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.Const;
import com.example.demo.annotation.UserLoginToken;
import com.example.demo.entity.Result;
import com.example.demo.entity.User;
import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;
import com.example.demo.util.AESUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;


/*
CREATE TABLE `user_t` (
  `id` bigint(32) NOT NULL COMMENT '主键',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `age` int(2) DEFAULT '1' COMMENT '年龄',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户表';


http://localhost:8081/user/addUser?id=18&userName=张三&password=123&age=73

http://localhost:8081/user/showUser?id=7

http://localhost:8081/user/login?id=7&password=1234

http://localhost:8081/user/getMessage

http://localhost:8081/user/getAll?pageNum=1&pageSize=3
 */
@Controller
@RequestMapping("/user")
public class UserController {
    //AuthorizationServerConfigurerAdapter
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @Autowired
    TokenService tokenService;

    @RequestMapping("/showUser")
    @ResponseBody
    //public User toIndex(HttpServletRequest request, Model model){
    public Object showUser(Integer id) {
        User user = this.userService.getUserById(id);

        try {
            String str = AESUtils.aesDecodeStr(AESUtils.aesEncryptStr("cs", Const.ENCRYPTED_MSG), Const.ENCRYPTED_MSG);
            if (Const.DEBUG)
                LOG.info(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.success(user);
    }

    @RequestMapping("/addUser")
    @ResponseBody
    public Object addUser(User user) throws Exception {
        /*User user = new User(Integer.parseInt(request.getParameter("id")),request.getParameter("user_name"),
                request.getParameter("password"),Integer.parseInt(request.getParameter("age")));*/

        userService.addUser(user);
        return Result.success(user);
    }


    @RequestMapping("/login")
    @ResponseBody
    public Object login(Integer id, String password) {
        JSONObject jsonObject = new JSONObject();
        User userForBase = userService.getUserById(id);
        if (userForBase == null) {
            jsonObject.put("message", "登录失败,用户不存在");
            return jsonObject;
        } else {
            if (!userForBase.getPassword().equals(password)) {
                jsonObject.put("message", "登录失败,密码错误");
                return jsonObject;
            } else {
                String token = tokenService.getToken(userForBase);
                jsonObject.put("token", token);
                jsonObject.put("user", userForBase);
                return jsonObject;
            }
        }

    }

    @RequestMapping("/getMessage")
    @ResponseBody
    @UserLoginToken
    public Object getMessage() {
        return Result.success("您已通过验证");
    }

    @RequestMapping("/getAll")
    @ResponseBody
    @UserLoginToken
    public Object list(Page<User> page) {

        /*ModelAndView result = new ModelAndView("index");
        List<User> countryList = userService.getAll(user);
        result.addObject("pageInfo", new PageInfo<>(countryList));
        result.addObject("queryParam", user);
        result.addObject("page", user.getPage());
        result.addObject("rows", user.getRows());
        return Result.success(result);*/
       /* List<User> countryList = userService.getAll(user);
        Map<String, Object> map = new HashMap<>();
        map.put("pageInfo", countryList);
        map.put("queryParam", user);
        map.put("page", user.getPage());
        map.put("rows", user.getRows());
        return Result.success(map);*/
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<User> list = userService.getAll();
        PageInfo<User> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo.getList());
    }

}
