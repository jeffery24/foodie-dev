package org.jeff.controller.center;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.jeff.controller.BaseController;
import org.jeff.pojo.vo.OrderStatusCountsVO;
import org.jeff.service.center.MyOrdersService;
import org.jeff.util.JEFFJSONResult;
import org.jeff.util.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Api(value = "用户中心我的订单", tags = {"用户中心我的订单相关接口"})
@RestController
@RequestMapping("myorders")
public class MyOrdersController extends BaseController {

    @Autowired
    private MyOrdersService myOrdersService;

    @PostMapping("/statusCounts")
    public JEFFJSONResult statusCounts(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId) {

        if (StringUtils.isBlank(userId)) {
            return JEFFJSONResult.errorMsg(null);
        }

        OrderStatusCountsVO result = myOrdersService.queryStatusCounts(userId);

        return JEFFJSONResult.ok(result);
    }

    @PostMapping("/trend")
    public JEFFJSONResult trend(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return JEFFJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = myOrdersService.getTrend(userId, page, pageSize);

        return JEFFJSONResult.ok(grid);
    }


    @PostMapping("/query")
    public JEFFJSONResult queryMyOrders(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderStatus", value = "订单状态", required = false)
            @RequestParam Integer orderStatus,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize

    ) {

        if (StringUtils.isBlank(userId)) {
            return JEFFJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = myOrdersService.queryMyOrders(userId, orderStatus, page, pageSize);
        return JEFFJSONResult.ok(grid);
    }


    // 商家发货没有后端，所以这个接口仅仅只是用于模拟
    @ApiOperation(value = "商家发货", notes = "商家发货", httpMethod = "GET")
    @GetMapping("/deliver")
    public JEFFJSONResult deliver(@RequestParam String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return JEFFJSONResult.errorMsg("不能为空");
        }
        myOrdersService.updateDeliverOrderStatus(orderId);
        return JEFFJSONResult.ok();
    }


    @ApiOperation(value = "确认收货", notes = "确认收货", httpMethod = "POST")
    @PostMapping("/confirmReceive")
    public JEFFJSONResult confirmReceive(
            @RequestParam @NotNull(message = "userId不能为空") String userId,
            @RequestParam @NotNull(message = "orderId不能为空") String orderId) {

        JEFFJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        boolean res = myOrdersService.updateReceiveOrderStatus(orderId);
        if (!res) {
            return JEFFJSONResult.errorMsg("订单确认收货失败");
        }

        return JEFFJSONResult.ok();
    }

    @ApiOperation(value = "删除订单", notes = "删除订单", httpMethod = "POST")
    @PostMapping("/deleteOrder")
    public JEFFJSONResult deleteOrder(
            @RequestParam @NotNull(message = "userId不能为空") String userId,
            @RequestParam @NotNull(message = "orderId不能为空") String orderId) {

        JEFFJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        boolean res = myOrdersService.deleteOrder(userId, orderId);
        if (!res) {
            return JEFFJSONResult.errorMsg("订单删除失败!");
        }

        return JEFFJSONResult.ok();
    }


}
