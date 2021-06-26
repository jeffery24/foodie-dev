package org.jeff.controller;

import org.apache.commons.lang3.StringUtils;
import org.jeff.service.UserService;
import org.jeff.utils.JEFFJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


}
