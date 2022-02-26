package org.jeff.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeff.my.mapper.MyMapper;
import org.jeff.pojo.OrderStatus;
import org.jeff.pojo.Orders;
import org.jeff.pojo.vo.MyOrdersVO;

import java.util.List;
import java.util.Map;

public interface OrdersMapperCustom extends MyMapper<Orders> {

    public List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String, Object> map);

    public int getMyOrderStatusCounts(@Param("paramsMap") Map<String, Object> map);


    public List<OrderStatus> queryTrend(Map<String, Object> map);
}