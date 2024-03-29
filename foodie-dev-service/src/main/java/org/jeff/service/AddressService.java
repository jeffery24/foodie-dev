package org.jeff.service;

import org.jeff.pojo.UserAddress;
import org.jeff.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {
    /**
     *
     * 根据用户id查询用户的收货地址列表
     */
    public List<UserAddress> queryAll(String userId);

    /**
     * 用户新增地址
     * @param addressBO
     */
    public void addNewUserAddress(AddressBO addressBO);

    /**
     * 修改新增地址
     * @param addressBO
     */
    public void updateUserAddress(AddressBO addressBO);

    /**
     * 删除新增地址
     * @param userId
     * @param addressId
     */
    public void deleteUserAddress(String userId, String addressId);

    /**
     * 设置为默认地址
     * @param userId
     * @param addressId
     */
    public void updateUserAddressToBeDefault(String userId, String addressId);

    /**
     * 根据用户id和地址id查询用户详细地址
     * @param userId
     * @param addressId
     * @return
     */
    public UserAddress queryUserAddress(String userId, String addressId);


}
