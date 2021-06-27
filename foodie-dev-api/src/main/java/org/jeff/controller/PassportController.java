package org.jeff.controller;

import org.apache.commons.lang3.StringUtils;
import org.jeff.bo.UserBo;
import org.jeff.pojo.Users;
import org.jeff.service.UserService;
import org.jeff.utils.JEFFJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/register")
    public JEFFJSONResult register(@RequestBody UserBo userBo) {
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
        if (password.length() < 6){
            return JEFFJSONResult.errorMsg("密码长度少于6位");
        }
        // 3. 判断两次密码是否一致
        if (!password.equals(confirmPwd)){
            return JEFFJSONResult.errorMsg("两次密码输入不一致");
        }
        // 4. 实现注册
        Users user = userService.createUser(userBo);
        return JEFFJSONResult.ok();
    }


}