package org.jeff.service;

import org.jeff.pojo.Items;
import org.jeff.pojo.ItemsImg;
import org.jeff.pojo.ItemsParam;
import org.jeff.pojo.ItemsSpec;
import org.jeff.pojo.vo.CommentVO;
import org.jeff.pojo.vo.ShopcartVO;
import org.jeff.util.PagedGridResult;

import java.util.List;


/**
 * 商品信息接口
 */
public interface ItemsService {

    /**
     * 根据商品ID查询详情
     *
     * @param itemId
     * @return
     */
    public Items queryItemById(String itemId);

    /**
     * 根据商品id查询商品图片列表
     *
     * @param itemId
     * @return
     */
    public List<ItemsImg> queryItemImgList(String itemId);

    /**
     * 根据商品id查询商品规格
     *
     * @param itemId
     * @return
     */
    public List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品id查询商品参数
     *
     * @param itemId
     * @return
     */
    public ItemsParam queryItemParamById(String itemId);

    /**
     * 根据商品id查询商品评价数
     *
     * @param itemId
     * @return
     */
    public CommentVO queryCommentCounts(String itemId);

    /**
     * 根据规格id查询商品规格
     *
     * @param specId
     * @return
     */
    public ItemsSpec queryItemSpec(String specId);

    /**
     * 根据商品id查询商品评价内容
     *
     * @param itemId
     * @return
     */
    public PagedGridResult queryPageComments(String itemId, Integer level, Integer page, Integer pageSize);


    /**
     * 根据关键词搜索商品列表
     *
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);

    /**
     * 通过分类id搜索商品列表
     *
     * @param catId
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize);

    /**
     * 通过规格ids查询购物车中商品数据
     *
     * @param specIds
     * @return
     */
    public List<ShopcartVO> queryItemBySpecIds(String specIds);

    /**
     * 通过商品id查询主图
     *
     * @param itemId
     * @return
     */
    public String queryItemMainImgById(String itemId);

    public void decreaseItemSpecStock(String itemSpecId, int buyCounts);
}
