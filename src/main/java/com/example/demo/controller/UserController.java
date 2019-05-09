package com.example.demo.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.Const;
import com.example.demo.annotation.UserLoginToken;
import com.example.demo.entity.CodeMsg;
import com.example.demo.entity.Page;
import com.example.demo.entity.Result;
import com.example.demo.entity.User;
import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;
import com.example.demo.util.RandomValidateCodeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
    public static final String AUTH_CODE_ATTRIBUTE = "RANDOM_CODE_ATTRIBUTE";
    //AuthorizationServerConfigurerAdapter
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @Autowired
    TokenService tokenService;

    @Autowired
    HttpSession httpSession;


    @RequestMapping(value = "/register", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "注册")
    public Object register(String id, String password) {
        String signPassword = JWT.create().withClaim("password", password).sign(Algorithm.HMAC256(Const.ENCRYPTED_MSG));
        return Result.success(userService.insert(new User(id, signPassword)));
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "登录")
    public Object login(String id, String password, String verifyCode) {
        User userForBase = userService.query(id);
        if (userForBase == null) {
            return Result.error(CodeMsg.OPERATION_FAILED.fillArgs("用户不存在"));
        } else {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(Const.ENCRYPTED_MSG)).build();
            String decodePassword = verifier.verify(userForBase.getPassword()).getClaims().get("password").asString();
            if (!password.equals(decodePassword)) {
                return Result.error(CodeMsg.OPERATION_FAILED.fillArgs("密码错误！"));
            } else {

                return //this.checkVerify(verifyCode);
                        Result.success(tokenService.getToken(userForBase));
            }
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除")
    public Object delete(String id) {
        return Result.success(userService.delete(id));
    }


    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取所有")
    public Object getAll(Page page) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<User> list = userService.getAll();
        return Result.success(new PageInfo<>(list).getList());
    }

    @RequestMapping(value = "/getVerify", method = RequestMethod.GET)
    @ApiOperation(value = "获取验证码")
    public void getVerify(HttpSession httpSession, HttpServletResponse response) throws IOException {
        RandomValidateCodeUtil randomValidateCode = new RandomValidateCodeUtil();
        RandomValidateCodeUtil.RandomObject randomObject = randomValidateCode.getRandomCode();
        httpSession.setAttribute(AUTH_CODE_ATTRIBUTE, randomObject.getCode());
        ImageIO.write(randomObject.getBufferedImage(), "jpeg", response.getOutputStream());
    }


    @RequestMapping(value = "/checkVerify", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "验证验证码")
    public Object checkVerify(String code) {
        Object attr = httpSession.getAttribute(AUTH_CODE_ATTRIBUTE);
        if (attr != null) {
            if (attr instanceof String && attr.equals(code)) {
                return Result.success();
            }
        }
        return Result.error(CodeMsg.OPERATION_FAILED.fillArgs("验证码错误"));

    }

}
