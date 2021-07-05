package org.jeff.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.jeff.pojo.Items;
import org.jeff.pojo.ItemsImg;
import org.jeff.pojo.ItemsParam;
import org.jeff.pojo.ItemsSpec;
import org.jeff.pojo.vo.ItemVO;
import org.jeff.service.ItemsService;
import org.jeff.utils.JEFFJSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "商品接口", tags = {"商品信息展示的相关接口"})
@RestController
@RequestMapping("items")
public class ItemsController {

    public static Logger logger = LoggerFactory.getLogger(ItemsController.class);

    @Autowired
    private ItemsService itemsService;


    @ApiOperation(value = "查询商品详情", notes = "查询商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public JEFFJSONResult queryItemsInfo(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @PathVariable String itemId) {

        if (StringUtils.isBlank(itemId)){
            return JEFFJSONResult.errorMsg(null);
        }

        Items items = itemsService.queryItemById(itemId);
        List<ItemsImg> itemsImgList = itemsService.queryItemImgList(itemId);
        List<ItemsSpec> itemsSpecList = itemsService.queryItemSpecList(itemId);
        ItemsParam itemsParam = itemsService.queryItemParamById(itemId);

        ItemVO itemVO = new ItemVO();
        itemVO.setItem(items);
        itemVO.setItemImgList(itemsImgList);
        itemVO.setItemSpecList(itemsSpecList);
        itemVO.setItemParams(itemsParam);

        return JEFFJSONResult.ok(itemVO);
    }



}
