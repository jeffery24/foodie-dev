package org.jeff.service;

import org.jeff.pojo.Items;
import org.jeff.pojo.ItemsImg;
import org.jeff.pojo.ItemsParam;
import org.jeff.pojo.ItemsSpec;

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


}
