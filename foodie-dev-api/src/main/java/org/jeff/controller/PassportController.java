package org.jeff.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jeff.pojo.bo.UserBo;
import org.jeff.pojo.Users;
import org.jeff.service.UserService;
import org.jeff.utils.CookieUtils;
import org.jeff.utils.JEFFJSONResult;
import org.jeff.utils.JsonUtils;
import org.jeff.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登录", tags = "用于注册登录相关接口")
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    /**
     * @param username 用户名
     * @return 响应状态
     * `@RequestParam` 声明不是请求路径参数
     */
    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public JEFFJSONResult usernameIsExist(@RequestParam String username) {
        // 1. 用户名不能为空
        if (StringUtils.isBlank(username)) {
            return JEFFJSONResult.errorException("用户名不能为空");
        }
        // 2. 查询的用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return JEFFJSONResult.errorMsg("用户名已存在");
        }
        // 3. 请求成功,用户名没有重复
        return JEFFJSONResult.ok();
    }


    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/register")
    public JEFFJSONResult register(@RequestBody UserBo userBo,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        String username = userBo.getUsername();
        String password = userBo.getPassword();
        String confirmPwd = userBo.getConfirmPassword();


        // 0. 用户名和密码不能为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirmPwd)) {
            return JEFFJSONResult.errorMsg("用户名和密码不能为空");
        }
        // 1. 查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return JEFFJSONResult.errorMsg("用户名已存在");
        }
        // 2. 密码长度不能少于6位
        if (password.length() < 6) {
            return JEFFJSONResult.errorMsg("密码长度少于6位");
        }
        // 3. 判断两次密码是否一致
        if (!password.equals(confirmPwd)) {
            return JEFFJSONResult.errorMsg("两次密码输入不一致");
        }
        // 4. 实现注册
        Users user = userService.createUser(userBo);

        // 将用户信息存入到 cookie 中,隐藏掉部分信息,并且加密
        user = setNullProperty(user);
        CookieUtils.setCookie(request, response, "userInfo",
                JsonUtils.objectToJson(user), true);
        return JEFFJSONResult.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public JEFFJSONResult login(@RequestBody UserBo userBo,
                                HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        String username = userBo.getUsername();
        String password = userBo.getPassword();


        // 0. 用户名和密码不能为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            return JEFFJSONResult.errorMsg("用户名和密码不能为空");
        }

        // 1. 实现登录
        Users user = userService.queryUsersForLogin(username, MD5Utils.getMD5Str(password));
        if (user == null) {
            return JEFFJSONResult.errorMsg("用户名或密码不正确");
        }
        // 将用户信息存入到 cookie 中,隐藏掉部分信息,并且加密
        user = setNullProperty(user);
        CookieUtils.setCookie(request, response, "userInfo",
                JsonUtils.objectToJson(user), true);
        return JEFFJSONResult.ok(user);
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/logout")
    public JEFFJSONResult logout(@RequestParam String userId, HttpServletRequest request, HttpServletResponse response) {

        // 清除用户相关信息的 cookie
        CookieUtils.deleteCookie(request, response, "user");

        // TODO 用户退出登录,需要清空购物车
        // TODO 分布式会话中需要清除用户数据
        return JEFFJSONResult.ok();
    }
}
