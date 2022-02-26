package org.jeff.controller.center;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jeff.pojo.Users;
import org.jeff.service.center.CenterUserService;
import org.jeff.util.JEFFJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "center - 用户中心", tags = {"用户中心展示的相关接口"})
@RestController
@RequestMapping("center")
public class CenterController {

    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation(value = "查询用户信息", notes = "查询用户信息")
    @GetMapping("userInfo")
    public JEFFJSONResult userInfo(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId) {

        Users user = centerUserService.queryUserInfo(userId);
        return JEFFJSONResult.ok(user);
    }



}
