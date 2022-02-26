package org.jeff.service.impl.center;

import com.github.pagehelper.PageHelper;
import org.jeff.enums.YesOrNo;
import org.jeff.mapper.*;
import org.jeff.pojo.OrderItems;
import org.jeff.pojo.OrderStatus;
import org.jeff.pojo.Orders;
import org.jeff.pojo.bo.center.OrderItemsCommentBO;
import org.jeff.pojo.vo.MyCommentVO;
import org.jeff.service.center.MyCommentsService;
import org.jeff.util.PagedGridResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyCommentsServiceImpl extends BaseServiceImpl implements MyCommentsService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems query = new OrderItems();
        query.setOrderId(orderId);
        return orderItemsMapper.select(query);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComments(String userId, String orderId, List<OrderItemsCommentBO> commentList) {

        // 添加评论
        commentList.forEach(comment -> comment.setCommentId(sid.nextShort()));
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        map.put("userId", userId);
        map.put("commentList", commentList);
        itemsCommentsMapperCustom.saveComments(map);

        // 更新订单 已评论
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setIsComment(YesOrNo.YES.type);
        orders.setUpdatedTime(new Date());
        ordersMapper.updateByPrimaryKeySelective(orders);

        // 更新订单状态 留言时间
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);

    }

    @Override
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        PageHelper.startPage(page, pageSize);
        List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);

        return setterPagedGrid(list, page);
    }


}
