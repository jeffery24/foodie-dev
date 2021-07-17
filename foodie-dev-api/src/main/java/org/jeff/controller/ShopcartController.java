package org.jeff.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jeff.pojo.bo.ShopcartBO;
import org.jeff.utils.JEFFJSONResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "购物车Controller",tags = "购物车相关接口")
@RequestMapping("shopcart")
@RestController
public class ShopcartController {


    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @GetMapping("/add")
    public JEFFJSONResult add(@RequestParam String cartId,
                      @RequestBody ShopcartBO shopcartBO ,
                      HttpServletRequest request,
                      HttpServletResponse response) {
        if (StringUtils.isBlank(cartId)){
            return JEFFJSONResult.errorMsg("");
        }

        System.out.println(shopcartBO);
        //TODO 前端用户在登录的情况下,添加购物车，同时会在后端添加Redis缓存
        return JEFFJSONResult.ok();
    }

    @ApiOperation(value = "删除购物车中商品数据", notes = "删除购物车中商品数据", httpMethod = "GET")
    @GetMapping("/del")
    public JEFFJSONResult del(@RequestParam String userId,
                              @RequestParam String itemSpecId ,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)){
            return JEFFJSONResult.errorMsg("参数不能为空");
        }

        //TODO 用户在页面删除购物车中数据,如果用户是登录状态,则需同步删除后端购物车中的数据
        return JEFFJSONResult.ok();
    }
}
