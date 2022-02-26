package org.jeff.service.center;

import org.jeff.pojo.Orders;
import org.jeff.pojo.vo.OrderStatusCountsVO;
import org.jeff.util.PagedGridResult;

public interface MyOrdersService {

    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize);

    public void updateDeliverOrderStatus(String orderId);

    public Orders queryMyOrder(String userId, String orderId);

    public boolean updateReceiveOrderStatus(String orderId);

    public boolean deleteOrder(String userId, String orderId);

    public OrderStatusCountsVO queryStatusCounts(String userId);

    public PagedGridResult getTrend(String userId, Integer page, Integer pageSize);

}
