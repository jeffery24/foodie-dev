package org.jeff.controller;

import org.jeff.util.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;

@ApiIgnore
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/get")
    public Object get(String key) {
        return redisOperator.get(key);
    }

    @GetMapping("/set")
    public Object save(String key, String value) {
        redisOperator.set(key, value);
        return "OK";
    }

    @GetMapping("/delete")
    public Object delete(String key) {
        redisOperator.del(key);
        return "OK";
    }

    /**
     * 批量查询：mget
     * 字符串类型
     * @param keys
     * @return
     */
    @GetMapping("/mget")
    public Object mget(String... keys) {
        List<String> keyList = Arrays.asList(keys);
        return redisOperator.mget(keyList);
    }

    /**
     * 批量查询：mget
     * 支持更多类型 更丰富操作
     * @param keys
     * @return
     */
    @GetMapping("/batchGet")
    public Object batchGet(String... keys) {
        List<String> keyList = Arrays.asList(keys);
        return redisOperator.batchGet(keyList);
    }



}
