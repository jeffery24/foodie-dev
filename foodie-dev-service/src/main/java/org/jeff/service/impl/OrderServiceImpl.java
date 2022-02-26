package org.jeff.service.impl;

import org.jeff.enums.OrderStatusEnum;
import org.jeff.enums.YesOrNo;
import org.jeff.mapper.OrderItemsMapper;
import org.jeff.mapper.OrderStatusMapper;
import org.jeff.mapper.OrdersMapper;
import org.jeff.pojo.*;
import org.jeff.pojo.bo.ShopcartBO;
import org.jeff.pojo.bo.SubmitOrderBo;
import org.jeff.pojo.vo.MerchantOrdersVO;
import org.jeff.pojo.vo.OrderVO;
import org.jeff.service.AddressService;
import org.jeff.service.ItemsService;
import org.jeff.service.OrderService;
import org.jeff.util.DateUtil;
import org.jeff.util.OrderNoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ItemsService itemsService;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public OrderVO createOrder(List<ShopcartBO> shopcartList, SubmitOrderBo submitOrderBo) {
        String userId = submitOrderBo.getUserId();
        String addressId = submitOrderBo.getAddressId();
        Integer payMethod = submitOrderBo.getPayMethod();
        String leftMsg = submitOrderBo.getLeftMsg();
        String itemSpecIds = submitOrderBo.getItemSpecIds();

        // 邮费设置为0,默认包邮,后续邮费规则增加可以通过数据库查询出来
        Integer postAmount = 0;
        //String orderId = sid.nextShort();
        String orderId = OrderNoUtils.getOrderNo();
        //1.新订单数据保存
        UserAddress address = addressService.queryUserAddress(userId, addressId);
        Orders newOrder = new Orders();
        newOrder.setId(orderId);
        newOrder.setUserId(userId);

        newOrder.setReceiverName(address.getReceiver());
        newOrder.setReceiverMobile(address.getMobile());
        newOrder.setReceiverAddress(address.getProvince() + " "
                + address.getCity() + " "
                + address.getDistrict() + " "
                + address.getDetail() + " "
        );

        //newOrder.setRealPayAmount();
        //newOrder.setTotalAmount();
        newOrder.setPostAmount(postAmount);

        newOrder.setPayMethod(payMethod);
        newOrder.setLeftMsg(leftMsg);

        newOrder.setIsComment(YesOrNo.NO.type);
        newOrder.setIsDelete(YesOrNo.NO.type);
        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(new Date());

        //2.循环根据商品ids保存订单商品数据
        String[] itemSpecArr = itemSpecIds.split(",");
        Integer totalAmount = 0; //商品原价累计
        Integer realPayAmount = 0; //优惠后的实际支付价格累计
        List<ShopcartBO> toBeRemovedShopcatdList = new ArrayList<>();
        for (String itemSpecId : itemSpecArr) {
            // 整合redis后，商品购买的数量重新从redis的购物车中获取
            ShopcartBO cartItem = getBuyCountsFromShopcart(shopcartList,itemSpecId);
            int buyCounts = cartItem.getBuyCounts();
            toBeRemovedShopcatdList.add(cartItem);

            // 2.1 根据规格id，查询规格的具体信息，主要获取价格
            ItemsSpec itemSpec = itemsService.queryItemSpec(itemSpecId);
            totalAmount += itemSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemSpec.getPriceDiscount() * buyCounts;
            // 2.2 根据商品id，获得商品信息以及商品图片
            String itemId = itemSpec.getItemId();
            Items item = itemsService.queryItemById(itemId);
            String imgUrl = itemsService.queryItemMainImgById(itemId);

            //2.3 循环保存子订单数据到数据库
            String subOrderId = sid.nextShort();
            OrderItems subOrderItem = new OrderItems();
            subOrderItem.setId(subOrderId);
            subOrderItem.setItemId(itemId);
            subOrderItem.setOrderId(orderId);
            subOrderItem.setItemImg(imgUrl);
            subOrderItem.setItemName(item.getItemName());
            subOrderItem.setBuyCounts(buyCounts);
            subOrderItem.setItemSpecId(itemSpecId);
            subOrderItem.setItemSpecName(itemSpec.getName());
            subOrderItem.setPrice(itemSpec.getPriceDiscount());

            orderItemsMapper.insert(subOrderItem);

            //2.4 用户提交订单后需要扣减库存
            itemsService.decreaseItemSpecStock(itemSpecId, buyCounts);
        }
        newOrder.setTotalAmount(totalAmount);
        newOrder.setRealPayAmount(realPayAmount);
        ordersMapper.insert(newOrder);

        //3.保存订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        orderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(orderStatus);

        //4. 构建商户订单,用户传递给支付中心
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setAmount(realPayAmount + postAmount); //实际支付金额 + 邮费
        merchantOrdersVO.setPayMethod(payMethod);

        //5. 构建自定义订单vo
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrdersVO(merchantOrdersVO);
        orderVO.setToBeRemovedShopcatdList(toBeRemovedShopcatdList);


        return orderVO;
    }

    /**
     * 从redis中的购物车里获取商品，目的：counts
     */
    private ShopcartBO getBuyCountsFromShopcart(List<ShopcartBO> shopcartList, String itemSpecId) {
        for (ShopcartBO cartItem : shopcartList) {
            if (itemSpecId.equals(cartItem.getSpecId())){
                return cartItem;
            }
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {

        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void closeOrder() {

        // 查询所有未支付订单,判断时间是否超过一天,超时关闭
        OrderStatus queryOrder = new OrderStatus();
        queryOrder.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        List<OrderStatus> list = orderStatusMapper.select(queryOrder);
        for (OrderStatus os : list) {
            // 获取订单创建时间
            Date createdTime = os.getCreatedTime();
            // 创建时间和当前时间对比
            int days = DateUtil.daysBetween(createdTime, new Date());
            if (days >= 1) {
                //关闭订单
                doCloseOrder(os.getOrderId());
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {
        return orderStatusMapper.selectByPrimaryKey(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    void doCloseOrder(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(OrderStatusEnum.CLOSE.type);
        orderStatus.setCloseTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }
}

























