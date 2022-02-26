package org.jeff.service.impl.center;

import com.github.pagehelper.PageHelper;
import org.jeff.enums.OrderStatusEnum;
import org.jeff.enums.YesOrNo;
import org.jeff.mapper.OrderStatusMapper;
import org.jeff.mapper.OrdersMapper;
import org.jeff.mapper.OrdersMapperCustom;
import org.jeff.pojo.OrderStatus;
import org.jeff.pojo.Orders;
import org.jeff.pojo.vo.MyOrdersVO;
import org.jeff.pojo.vo.OrderStatusCountsVO;
import org.jeff.service.center.MyOrdersService;
import org.jeff.util.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyOrdersServiceImpl extends BaseServiceImpl implements MyOrdersService {

    @Autowired
    private OrdersMapperCustom ordersMapperCustom;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrdersMapper ordersMapper;


    @Override
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        if (orderStatus != null) {
            map.put("orderStatus", orderStatus);
        }

        PageHelper.startPage(page, pageSize);

        List<MyOrdersVO> myOrdersVOS = ordersMapperCustom.queryMyOrders(map);
        return setterPagedGrid(myOrdersVOS, page);
    }

    // 暂时做成修改状态 --> 发货 ,后面根据具体业务去做实现
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateDeliverOrderStatus(String orderId) {
        OrderStatus updateOrder = new OrderStatus();
        updateOrder.setOrderStatus(OrderStatusEnum.WAIT_RECEIVE.type);
        updateOrder.setDeliverTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_DELIVER.type);

        orderStatusMapper.updateByExample(updateOrder, example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Orders queryMyOrder(String userId, String orderId) {

        Orders orderCondition = new Orders();
        orderCondition.setUserId(userId);
        orderCondition.setId(orderId);
        orderCondition.setIsDelete(YesOrNo.NO.type);

        return ordersMapperCustom.selectOne(orderCondition);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean updateReceiveOrderStatus(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.SUCCESS.type);
        orderStatus.setSuccessTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);
        criteria.andEqualTo("orderId", orderId);
        int result = orderStatusMapper.updateByExampleSelective(orderStatus, example);
        return result == 1;
    }

    @Override
    public boolean deleteOrder(String userId, String orderId) {
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setUserId(userId);
        orders.setIsDelete(YesOrNo.YES.type);

        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("id", orderId);
        int col = ordersMapper.updateByExampleSelective(orders, example);
        return col == 1;
    }

    @Override
    public OrderStatusCountsVO queryStatusCounts(String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        map.put("orderStatus", OrderStatusEnum.WAIT_PAY.type);
        int waitPayCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_DELIVER.type);
        int waitDeliverCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);
        int waitReceiveCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.SUCCESS.type);
        map.put("isComment", YesOrNo.NO.type);
        int waitCommentCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        return new OrderStatusCountsVO(
                waitPayCounts, waitDeliverCounts, waitReceiveCounts, waitCommentCounts);
    }


    @Override
    public PagedGridResult getTrend(String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        PageHelper.startPage(page,pageSize);
        List<OrderStatus> orderStatusList = ordersMapperCustom.queryTrend(map);
        return setterPagedGrid(orderStatusList,page);
    }

}
