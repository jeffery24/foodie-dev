package org.jeff.service;

import org.jeff.pojo.Items;
import org.jeff.pojo.ItemsImg;
import org.jeff.pojo.ItemsParam;
import org.jeff.pojo.ItemsSpec;
import org.jeff.pojo.vo.CommentVO;
import org.jeff.utils.PagedGridResult;

import java.util.List;


/**
 * 商品信息接口
 */
public interface ItemsService {

    /**
     * 根据商品ID查询详情
     * @param itemId
     * @return
     */
    public Items queryItemById(String itemId);

    /**
     * 根据商品id查询商品图片列表
     * @param itemId
     * @return
     */
    public List<ItemsImg> queryItemImgList(String itemId);

    /**
     * 根据商品id查询商品规格
     * @param itemId
     * @return
     */
    public List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品id查询商品参数
     * @param itemId
     * @return
     */
    public ItemsParam queryItemParamById(String itemId);

    /**
     * 根据商品id查询商品评价数
     * @param itemId
     * @return
     */
    public CommentVO queryCommentCounts(String itemId);

    /**
     * 根据商品id查询商品评价内容
     * @param itemId
     * @return
     */
    public PagedGridResult queryPageComments(String itemId, Integer level, Integer page, Integer pageSize);


    /**
     * 根据关键词搜索商品列表
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);

    /**
     * 通过分类id搜索商品列表
     * @param catId
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize);
}
