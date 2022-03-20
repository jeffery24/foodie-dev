package org.jeff.controller;

import org.jeff.pojo.Orders;
import org.jeff.pojo.Users;
import org.jeff.pojo.vo.UsersVO;
import org.jeff.service.center.MyOrdersService;
import org.jeff.util.JEFFJSONResult;
import org.jeff.util.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.UUID;

/**
 * 定义一些通用的参数
 */
@Controller
public class BaseController {

    public static final String FOODIE_SHOPCART = "shopcart";
    public static final String REDIS_USER_TOKEN = "redis_user_token";

    public static final Integer COMMON_PAGE_SIZE = 10;
    public static final Integer SEARCH_PAGE_SIZE = 20;

    // 支付中心的调用地址
    String paymentUrl = "https://zsj.zone/payment/createMerchantOrder";        // produce


    // 支付成功 -> 支付中心 -> 系统平台
    //                    |-> 回调通知url
    String payReturnUrl = "http://api.z.zsj.zone/foodie-dev-api/foodie-dev-api/orders/notifyMerchantOrderPaid";

    @Autowired
    public MyOrdersService myOrdersService;
    @Autowired
    private RedisOperator redisOperator;

    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     *
     * @return
     */
    public JEFFJSONResult checkUserOrder(String userId, String orderId) {
        Orders orders = myOrdersService.queryMyOrder(userId, orderId);
        if (orders == null) {
            return JEFFJSONResult.errorMsg("订单不存在!");
        }
        return JEFFJSONResult.ok(orders);
    }

    protected UsersVO conventUserVO(Users user) {
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + user.getId(), uniqueToken);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }


}
