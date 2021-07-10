package org.jeff.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.jeff.pojo.Items;
import org.jeff.pojo.ItemsImg;
import org.jeff.pojo.ItemsParam;
import org.jeff.pojo.ItemsSpec;
import org.jeff.pojo.vo.CommentVO;
import org.jeff.pojo.vo.ItemVO;
import org.jeff.service.ItemsService;
import org.jeff.utils.JEFFJSONResult;
import org.jeff.utils.PagedGridResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品接口", tags = {"商品信息展示的相关接口"})
@RestController
@RequestMapping("items")
public class ItemsController extends BaseController{

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

    @ApiOperation(value = "查询商品评价数", notes = "查询商品评价数", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public JEFFJSONResult commentLevel(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId) {

        if (StringUtils.isBlank(itemId)){
            return JEFFJSONResult.errorMsg(null);
        }

        CommentVO commentVO = itemsService.queryCommentCounts(itemId);

        return JEFFJSONResult.ok(commentVO);
    }


    @ApiOperation(value = "查询商品评论", notes = "查询商品评论", httpMethod = "GET")
    @GetMapping("/comments")
    public JEFFJSONResult queryItemComment(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level", value = "评价等级", required = false)
            @RequestParam Integer level,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize
            ) {

        if (StringUtils.isBlank(itemId)){
            return JEFFJSONResult.errorMsg(null);
        }
        if (page==null){
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMENT_PAGE_SIZE;
        }

        PagedGridResult grid = itemsService.queryPageComments(itemId, level, page, pageSize);

        return JEFFJSONResult.ok(grid);
    }


    @ApiOperation(value = "搜索商品列表", notes = "搜索商品列表", httpMethod = "GET")
    @GetMapping("/search")
    public JEFFJSONResult search(
            @ApiParam(name = "keywords", value = "关键词", required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort", value = "排序规则", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize
    ) {

        if (StringUtils.isBlank(keywords)){
            return JEFFJSONResult.errorMsg(null);
        }
        if (page==null){
            page = 1;
        }
        if (pageSize == null) {
            pageSize = SEARCH_PAGE_SIZE;
        }

        PagedGridResult grid = itemsService.searchItems(keywords, sort, page, pageSize);

        return JEFFJSONResult.ok(grid);
    }


    @ApiOperation(value = "通过分类id搜索商品列表", notes = "通过分类id搜索商品列表", httpMethod = "GET")
    @GetMapping("/catItems")
    public JEFFJSONResult catItems(
            @ApiParam(name = "catId", value = "三级分类ID", required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort", value = "排序规则", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize
    ) {

        if (catId == null){
            return JEFFJSONResult.errorMsg(null);
        }
        if (page==null){
            page = 1;
        }
        if (pageSize == null) {
            pageSize = SEARCH_PAGE_SIZE;
        }

        PagedGridResult grid = itemsService.searchItems(catId, sort, page, pageSize);

        return JEFFJSONResult.ok(grid);
    }



}
