package org.jeff.controller.center;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.jeff.controller.BaseController;
import org.jeff.enums.YesOrNo;
import org.jeff.pojo.OrderItems;
import org.jeff.pojo.Orders;
import org.jeff.pojo.bo.center.OrderItemsCommentBO;
import org.jeff.service.center.MyCommentsService;
import org.jeff.util.JEFFJSONResult;
import org.jeff.util.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "用户中心评价模块", tags = {"用户中心评价模块相关接口"})
@RestController
@RequestMapping("mycomments")
public class MyCommentsController extends BaseController {

    @Autowired
    private MyCommentsService myCommentsService;

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/pending")
    public JEFFJSONResult pending(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单ID", required = true)
            @RequestParam String orderId) {

        // 判断用户和订单是否关联
        JEFFJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        Orders order = (Orders) checkResult.getData();
        if (order.getIsComment() == YesOrNo.YES.type){
            return JEFFJSONResult.errorMsg("该笔订单已经评价");
        }
        List<OrderItems> list = myCommentsService.queryPendingComment(orderId);

        return JEFFJSONResult.ok(list);
    }

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/saveList")
    public JEFFJSONResult saveList(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单ID", required = true)
            @RequestParam String orderId,
            @RequestBody List<OrderItemsCommentBO> commentList) {

        // 判断用户和订单是否关联
        JEFFJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        if (commentList == null || commentList.isEmpty() || commentList.size() == 0){
            return JEFFJSONResult.errorMsg("评论内容不能为空！");
        }

        myCommentsService.saveComments(userId,orderId,commentList);

        return JEFFJSONResult.ok();
    }

    @ApiOperation(value = "查询我的评价", notes = "查询我的评价", httpMethod = "POST")
    @PostMapping("/query")
    public JEFFJSONResult query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)){
            return JEFFJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid =  myCommentsService.queryMyComments(userId, page, pageSize);
        return  JEFFJSONResult.ok(grid);

    }






















}