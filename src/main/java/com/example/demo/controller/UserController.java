package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.Const;
import com.example.demo.annotation.UserLoginToken;
import com.example.demo.entity.CodeMsg;
import com.example.demo.entity.Page;
import com.example.demo.entity.Result;
import com.example.demo.entity.User;
import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;
import com.example.demo.util.AESUtils;
import com.example.demo.util.RandomValidateCodeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

http://127.0.0.1:8081/swagger-ui.html

http://127.0.0.1:8081/user/getVerify
 */


@Controller
@RequestMapping("/user")
public class UserController {
    public static final String RANDOM_CODE_ATTRIBUTE = "RANDOM_CODE_ATTRIBUTE";
    //AuthorizationServerConfigurerAdapter
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @Autowired
    TokenService tokenService;

    @RequestMapping(value = "/showUser", method = RequestMethod.GET)
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

    @RequestMapping(value = "/addUser", method = RequestMethod.GET)
    @ResponseBody
    public Object addUser(User user) throws Exception {
        /*User user = new User(Integer.parseInt(request.getParameter("id")),request.getParameter("user_name"),
                request.getParameter("password"),Integer.parseInt(request.getParameter("age")));*/

        userService.addUser(user);
        return Result.success(user);
    }


    @ApiOperation(value = "登录", notes = "fsdf")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
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

    @RequestMapping(value = "/getMessage", method = RequestMethod.GET)
    @ResponseBody
    @UserLoginToken
    public Object getMessage() {
        return Result.success("您已通过验证");
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ResponseBody
    @UserLoginToken
    public Object list(Page page) {

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


    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 生成验证码
     */
    @RequestMapping(value = "/getVerify", method = RequestMethod.GET)
    @ApiOperation(value = "获取验证码")
    public void getVerify(HttpSession httpSession, HttpServletResponse response) throws IOException {
        RandomValidateCodeUtil randomValidateCode = new RandomValidateCodeUtil();
        RandomValidateCodeUtil.RandomObject randomObject = randomValidateCode.getRandomCode();
        httpSession.setAttribute(RANDOM_CODE_ATTRIBUTE, randomObject.getCode());
        ImageIO.write(randomObject.getBufferedImage(), "jpeg", response.getOutputStream());
    }


    /**
     * 校验验证码
     */
    @RequestMapping(value = "/checkVerify", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "验证验证码")
    public Object checkVerify(@RequestParam String verifyInput, HttpSession session) {
        Object attr = session.getAttribute(RANDOM_CODE_ATTRIBUTE);
        if (attr != null) {
            if (attr instanceof String && attr.equals(verifyInput)) {
                return Result.success(true);
            }
        }
        return Result.error(CodeMsg.OPERATION_FAILD.fillArgs(false));

    }


}
