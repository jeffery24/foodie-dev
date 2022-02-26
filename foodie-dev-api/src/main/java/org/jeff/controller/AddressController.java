package org.jeff.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jeff.pojo.UserAddress;
import org.jeff.pojo.bo.AddressBO;
import org.jeff.service.AddressService;
import org.jeff.util.JEFFJSONResult;
import org.jeff.util.MobileEmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "地址相关接口", tags = "地址相关的api接口")
@RequestMapping("address")
@RestController
public class AddressController {

    /**
     * 用户地址相关接口
     * 1. 查询用户的所有收货地址
     * 2. 添加地址
     * 3. 修改地址
     * 4. 删除地址
     * 5. 设置地址为默认的
     */

    @Autowired
    private AddressService addressService;

    @ApiOperation(value = "根据用户id查询收货地址列表", notes = "根据用户id查询收货地址列表", httpMethod = "POST")
    @PostMapping("/list")
    public JEFFJSONResult list(@RequestParam String userId) {

        if (StringUtils.isBlank(userId)) {
            return JEFFJSONResult.errorMsg("");
        }

        List<UserAddress> list = addressService.queryAll(userId);
        return JEFFJSONResult.ok(list);
    }

    @ApiOperation(value = "用户新增地址", notes = "用户新增地址", httpMethod = "POST")
    @PostMapping("/add")
    public JEFFJSONResult add(@RequestBody AddressBO addressBO) {

        JEFFJSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }

        addressService.addNewUserAddress(addressBO);

        return JEFFJSONResult.ok();
    }

    private JEFFJSONResult checkAddress(AddressBO addressBO) {
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return JEFFJSONResult.errorMsg("收货人不能为空");
        }
        if (receiver.length() > 12) {
            return JEFFJSONResult.errorMsg("收货人姓名不能太长");
        }

        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return JEFFJSONResult.errorMsg("收货人手机号不能为空");
        }
        if (mobile.length() != 11) {
            return JEFFJSONResult.errorMsg("收货人手机号长度不正确");
        }
        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk) {
            return JEFFJSONResult.errorMsg("收货人手机号格式不正确");
        }

        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province) ||
                StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) ||
                StringUtils.isBlank(detail)) {
            return JEFFJSONResult.errorMsg("收货地址信息不能为空");
        }

        return JEFFJSONResult.ok();
    }

    @ApiOperation(value = "用户修改地址", notes = "用户修改地址", httpMethod = "POST")
    @PostMapping("/update")
    public JEFFJSONResult update(@RequestBody AddressBO addressBO) {

        if (StringUtils.isBlank(addressBO.getAddressId())) {
            return JEFFJSONResult.errorMsg("修改地址错误：addressId不能为空");
        }

        JEFFJSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }

        addressService.updateUserAddress(addressBO);

        return JEFFJSONResult.ok();
    }

    @ApiOperation(value = "用户新增地址", notes = "用户新增地址", httpMethod = "POST")
    @PostMapping("/delete")
    public JEFFJSONResult delete(
            @RequestParam String userId,
            @RequestParam String addressId) {


        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return JEFFJSONResult.errorMsg("");
        }

        addressService.deleteUserAddress(userId,addressId);

        return JEFFJSONResult.ok();
    }

    @ApiOperation(value = "用户设置默认地址", notes = "用户设置默认地址", httpMethod = "POST")
    @PostMapping("/setDefalut")
    public JEFFJSONResult setDefalut(
            @RequestParam String userId,
            @RequestParam String addressId) {


        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return JEFFJSONResult.errorMsg("");
        }

        addressService.updateUserAddressToBeDefault(userId,addressId);

        return JEFFJSONResult.ok();
    }




}
