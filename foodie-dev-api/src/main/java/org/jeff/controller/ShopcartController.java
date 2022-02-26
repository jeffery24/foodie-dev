package org.jeff.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jeff.pojo.bo.ShopcartBO;
import org.jeff.util.JEFFJSONResult;
import org.jeff.util.JsonUtils;
import org.jeff.util.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "购物车Controller", tags = "购物车相关接口")
@RequestMapping("shopcart")
@RestController
public class ShopcartController extends BaseController {

    @Autowired
    private RedisOperator redisOperator;


    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public JEFFJSONResult add(@RequestParam String userId,
                              @RequestBody ShopcartBO shopcartBO,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        if (StringUtils.isBlank(userId)) {
            return JEFFJSONResult.errorMsg("");
        }

        System.out.println(shopcartBO);

        // step1. 获取缓存中的购物车数据
        String userShopcartKey = FOODIE_SHOPCART + ":" + userId;
        String shopcartJson = redisOperator.get(userShopcartKey);
        List<ShopcartBO> shopcartList;
        // Redis购物车中存在商品累加数量,不存在新增
        if (StringUtils.isNotBlank(shopcartJson)) {
            // redis 中有购物车了,处理
            shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
            boolean isHaving = false;
            // 已有 增加数量
            for (ShopcartBO item : shopcartList) {
                String tmpSpecId = item.getSpecId();
                if (tmpSpecId.equals(shopcartBO.getSpecId())) {
                    item.setBuyCounts(item.getBuyCounts() + shopcartBO.getBuyCounts());
                    isHaving = true;
                }
            }
            // 增加新的商品
            if (!isHaving) {
                shopcartList.add(shopcartBO);
            }

        } else {
            // 没有购物车
            shopcartList = new ArrayList<>();
            shopcartList.add(shopcartBO);
        }

        // step2. 覆盖缓存中的购物车数据
        redisOperator.set(userShopcartKey, JsonUtils.objectToJson(shopcartList));

        return JEFFJSONResult.ok();
    }


    @ApiOperation(value = "删除购物车中商品数据", notes = "删除购物车中商品数据", httpMethod = "GET")
    @GetMapping("/del")
    public JEFFJSONResult del(@RequestParam String userId,
                              @RequestParam String itemSpecId,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return JEFFJSONResult.errorMsg("参数不能为空");
        }

        // 用户在页面删除购物车中数据,如果用户是登录状态,则需同步删除后端购物车中的数据
        // step1. 获取缓存中的购物车数据,进行处理
        String userShopcartKey = FOODIE_SHOPCART + ":" + userId;
        String shopcartJson = redisOperator.get(userShopcartKey);
        if (StringUtils.isNotBlank(shopcartJson)) {
            List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
            for (ShopcartBO item : shopcartList) {
                String tmpSpecId = item.getSpecId();
                if (tmpSpecId.equals(itemSpecId)) {
                    shopcartList.remove(item);
                    // 暂时是单个删除,直接跳出即可,也可以扩展成 itemSpecId 批量删除
                    break;
                }
            }
            // step2. 覆盖缓存中的购物车数据
            redisOperator.set(userShopcartKey, JsonUtils.objectToJson(shopcartList));
        }
        return JEFFJSONResult.ok();
    }
}
