package org.jeff.pojo.vo;

import org.jeff.pojo.bo.ShopcartBO;

import java.util.List;

public class OrderVO {

    private String OrderId;         // 商户订单号
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopcartBO> toBeRemovedShopcatdList;

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public void setMerchantOrdersVO(MerchantOrdersVO merchantOrdersVO) {
        this.merchantOrdersVO = merchantOrdersVO;
    }

    public List<ShopcartBO> getToBeRemovedShopcatdList() {
        return toBeRemovedShopcatdList;
    }

    public void setToBeRemovedShopcatdList(List<ShopcartBO> toBeRemovedShopcatdList) {
        this.toBeRemovedShopcatdList = toBeRemovedShopcatdList;
    }
}