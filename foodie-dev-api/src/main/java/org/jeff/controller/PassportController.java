package org.jeff.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jeff.pojo.Users;
import org.jeff.pojo.bo.ShopcartBO;
import org.jeff.pojo.bo.UserBo;
import org.jeff.pojo.vo.UsersVO;
import org.jeff.service.UserService;
import org.jeff.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "注册登录", tags = "用于注册登录相关接口")
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

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
        //user = setNullProperty(user);
        // 生成用户token,放入redis会话
        UsersVO usersVO = conventUserVO(user);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);

        //同步购物车数据
        syncShopcartData(user.getId(), request, response);

        return JEFFJSONResult.ok();
    }

    @CrossOrigin
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
        //user = setNullProperty(user);

        //生成用户token,放入redis会话
        UsersVO usersVO = conventUserVO(user);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);


        //同步购物车数据
        syncShopcartData(user.getId(), request, response);
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

    /**
     * 注册登录后需要同步购物车数据
     * <p>
     * 1. redis中无数据
     * cookie中无数据，不进行任何操作
     * cookie中有数据，直接存入redis中
     * 2. redis中有数据，
     * cookie中无数据，将redis中的数据放到cookie中
     * cookie中有数据
     * cookie中的某个商品redis也存在
     * 以cookie的数据为主 删除redis中的
     * 把cookie的数据覆盖到redis中（参考京东）
     * 3. 同步到redis中，覆盖本地cookie数据，保证购物车数据是最新的
     */
    private void syncShopcartData(String userId, HttpServletRequest request,
                                  HttpServletResponse response) {

        String shopcartRedisKey = FOODIE_SHOPCART + ":" + userId;
        // get shopping cart data from redis
        String shopcartJsonRedis = redisOperator.get(shopcartRedisKey);

        // get shopping cart data from cookie
        String shopcartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);

        // no shopping cart data in redis
        if (StringUtils.isBlank(shopcartJsonRedis)) {
            if (StringUtils.isNotBlank(shopcartStrCookie)) {
                redisOperator.set(shopcartRedisKey, shopcartStrCookie);
            }
        } else {
            // redis not empty and cookie not empty merge shopping cart data from redis and cookie
            if (StringUtils.isNotBlank(shopcartStrCookie)) {

                List<ShopcartBO> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopcartListCookie = JsonUtils.jsonToList(shopcartStrCookie, ShopcartBO.class);
                // 定义一个待删除list
                List<ShopcartBO> pendingDeleteList = new ArrayList<>();

                for (ShopcartBO redisShopcart : shopcartListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();

                    for (ShopcartBO cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();

                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖购买数据,参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            pendingDeleteList.add(redisShopcart);
                        }

                    }
                }
                //从现有的的cookie中删除对于覆盖过的数据
                shopcartListCookie.removeAll(pendingDeleteList);
                //合并redis和cookie的购物车数据
                shopcartListRedis.addAll(shopcartListCookie);
                //更新到redis和cookie
                redisOperator.set(shopcartRedisKey, JsonUtils.objectToJson(shopcartListRedis));
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartListRedis),true);
            } else {
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopcartJsonRedis,true);
            }
        }
    }

    @ApiOperation(value = "用户退出", notes = "用户退出", httpMethod = "POST")
    @PostMapping("/logout")
    public JEFFJSONResult logout(@RequestParam String userId, HttpServletRequest request, HttpServletResponse response) {

        // 清除用户相关信息的 cookie
        CookieUtils.deleteCookie(request, response, "user");

        // 用户退出登录,需要清空购物车
        CookieUtils.deleteCookie(request, response, FOODIE_SHOPCART);

        // 分布式会话中需要清除用户数据
        redisOperator.del(redisOperator + ":" + userId);

        return JEFFJSONResult.ok();
    }
}
