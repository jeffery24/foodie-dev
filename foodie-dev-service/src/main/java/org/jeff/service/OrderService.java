package org.jeff.service;

import org.jeff.pojo.OrderStatus;
import org.jeff.pojo.bo.ShopcartBO;
import org.jeff.pojo.bo.SubmitOrderBo;
import org.jeff.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {

    /**
     * 创建订单
     *
     * @param submitOrderBo
     * @return
     */
    public OrderVO createOrder(List<ShopcartBO> shopcartList, SubmitOrderBo submitOrderBo);


    /**
     * 修改订单状态
     *
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 关闭超时未支付订单
     */
    public void closeOrder();

    /**
     * 查询订单状态
     *
     * @param orderId 订单ID
     * @return 订单状态
     */
    public OrderStatus queryOrderStatusInfo(String orderId);
}
