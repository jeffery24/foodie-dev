package org.jeff.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.jeff.enums.YesOrNo;
import org.jeff.pojo.Carousel;
import org.jeff.pojo.Category;
import org.jeff.pojo.vo.CategoryVO;
import org.jeff.pojo.vo.NewItemsVo;
import org.jeff.service.CarouselService;
import org.jeff.service.CategoryService;
import org.jeff.util.JEFFJSONResult;
import org.jeff.util.JsonUtils;
import org.jeff.util.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    public static Logger logger = LoggerFactory.getLogger(IndexController.class);

    public static final String INDEX_CAROUSEL_KEY = "index_carousel_key";
    public static final String INDEX_CATS_KEY = "index_cats_key";


    @Autowired
    private CarouselService carouselService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public JEFFJSONResult carousel() {

        String indexCarouselStr = redisOperator.get(INDEX_CAROUSEL_KEY);
        List<Carousel> carouselList;

        if (StringUtils.isBlank(indexCarouselStr)) {
            carouselList = carouselService.queryAll(YesOrNo.YES.type);
            redisOperator.set(INDEX_CAROUSEL_KEY, JsonUtils.objectToJson(carouselList));
        } else {
            carouselList = JsonUtils.jsonToList(indexCarouselStr, Carousel.class);
        }

        return JEFFJSONResult.ok(carouselList);
    }
    /**
     * 何时更新呢
     * 1. 后台运营系统，一旦广告（轮播图）发送变更，就可以删除缓存，然后重置
     * 2. 定时重置，例如每天凌晨三点
     * 3. 每个轮播图都可能是一个广告，每个广告可能会有一个过期时间，过期了，重置
     */


    /**
     * 首页分类展示需求：
     * 1. 第一次刷新主页查询大分类，渲染展示到首页
     * 2. 如果鼠标上移到大分类，则加载器子分类内容，如果已存在子分类，则不需要继续加载（懒加载）
     */
    @ApiOperation(value = "获取商品分类(一级分类)", notes = "获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public JEFFJSONResult cats() {

        List<Category> list;
        String indexCatsStr = redisOperator.get(INDEX_CATS_KEY);

        if (StringUtils.isBlank(indexCatsStr)) {
            list = categoryService.queryAllRootLevelCat();
            redisOperator.set(INDEX_CATS_KEY, JsonUtils.objectToJson(list));
        } else {
            list = JsonUtils.jsonToList(indexCatsStr, Category.class);
        }

        return JEFFJSONResult.ok(list);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public JEFFJSONResult subCat(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return JEFFJSONResult.errorMsg("分类不存在");
        }

        String catKey = "subCat:" + rootCatId;
        String catStr = redisOperator.get(catKey);
        List<CategoryVO> list;
        if (StringUtils.isBlank(catStr)) {
            list = categoryService.getSubCatList(rootCatId);
            String subCatDataJsonStr = JsonUtils.objectToJson(list);

            /*
                查询的key redis不存在
                对应的id数据库也不存在
                此时被非法用户大量请求攻击,会造成缓存击穿,所有的请求打在DB上
                造成宕机现象,影响系统运行
                解决方案：把空的数据也缓存起来，比如空字符串，空对象，空数组或list

             */
            if (CollectionUtils.isEmpty(list)) {
                redisOperator.set(catKey, subCatDataJsonStr, 5 * 60);
            } else {
                redisOperator.set(catKey, subCatDataJsonStr);
            }
        } else {
            list = JsonUtils.jsonToList(catStr, CategoryVO.class);
        }

        return JEFFJSONResult.ok(list);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes = "查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public JEFFJSONResult sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return JEFFJSONResult.errorMsg("分类不存在");
        }

        List<NewItemsVo> list = categoryService.getSixNewItemsLazy(rootCatId);
        return JEFFJSONResult.ok(list);
    }

}
