package org.jeff.config;

import org.jeff.service.OrderService;
import org.jeff.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderJob {

    @Autowired
    private OrderService orderService;

    //@Scheduled(cron = "0/3 * * * * ?")
    public void closeOrder() {
        orderService.closeOrder();
        System.out.println("定时关闭未支付的订单" + DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));
    }
}
