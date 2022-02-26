package org.jeff.service.center;

import org.jeff.pojo.OrderItems;
import org.jeff.pojo.bo.center.OrderItemsCommentBO;
import org.jeff.util.PagedGridResult;

import java.util.List;

public interface MyCommentsService {

    public List<OrderItems> queryPendingComment(String orderId);

    public void saveComments(String userId, String orderId, List<OrderItemsCommentBO> commentList);

    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize);

}
