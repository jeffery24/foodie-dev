package org.jeff.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jeff.enums.OrderStatusEnum;
import org.jeff.pojo.OrderStatus;
import org.jeff.pojo.bo.ShopcartBO;
import org.jeff.pojo.bo.SubmitOrderBo;
import org.jeff.pojo.vo.MerchantOrdersVO;
import org.jeff.pojo.vo.OrderVO;
import org.jeff.service.OrderService;
import org.jeff.util.CookieUtils;
import org.jeff.util.JEFFJSONResult;
import org.jeff.util.JsonUtils;
import org.jeff.util.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Api(value = "订单相关接口", tags = "订单相关的api接口")
@RequestMapping("orders")
@RestController
public class OrdersController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redisOperator;


    @ApiOperation(value = "创建订单", notes = "创建订单", httpMethod = "POST")
    @PostMapping("/create")
    public JEFFJSONResult create(@RequestBody SubmitOrderBo submitOrderBo, HttpServletRequest request,
                                 HttpServletResponse response) {

        System.out.println(submitOrderBo.toString());

        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + submitOrderBo.getUserId());
        if (StringUtils.isBlank(shopcartJson)) {
            return JEFFJSONResult.errorMsg("购物数据不正确");
        }

        logger.info("shop cart data: {}",shopcartJson);
        List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);

        //1. 创建一个新订单
        OrderVO orderVO = orderService.createOrder(shopcartList, submitOrderBo);
        String orderId = orderVO.getOrderId();
        //2. 从购物车中移除已提交订单相关商品数据
        shopcartList.removeAll(orderVO.getToBeRemovedShopcatdList());
        redisOperator.set(FOODIE_SHOPCART + ":" + submitOrderBo.getUserId(), JsonUtils.objectToJson(shopcartList));

        // 完善购物车中的已结算商品清除，并且同步到前端的cookie
        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartList), true);

        //3. 向支付中心发起提交订单,用于保存支付中心订单数据
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId", "imooc");
        headers.add("password", "imooc");

        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO, headers);

        ResponseEntity<JEFFJSONResult> responseEntity =
                restTemplate.postForEntity(paymentUrl,
                        entity, JEFFJSONResult.class);
        JEFFJSONResult paymentResult = responseEntity.getBody();
        if (paymentResult != null && paymentResult.getStatus() != 200) {
            logger.error("发送错误：{}", paymentResult.getMsg());
            return JEFFJSONResult.errorMsg("支付中心订单创建失败,请联系管理员！");
        }

            return JEFFJSONResult.ok(orderId);
    }


    @PostMapping("/notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @PostMapping("getPaidOrderInfo")
    public JEFFJSONResult getPaidOrderInfo(String orderId) {
        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return JEFFJSONResult.ok(orderStatus);
    }


}
