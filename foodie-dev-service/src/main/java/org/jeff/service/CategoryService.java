package org.jeff.service;

import org.jeff.pojo.Category;
import org.jeff.pojo.vo.CategoryVO;
import org.jeff.pojo.vo.NewItemsVo;

import java.util.List;

public interface CategoryService {
    /**
     * 查询所有一级分类
     */
    public List<Category> queryAllRootLevelCat();

    /**
     * 根据一级分类id查询子子分类信息
     */
    public List<CategoryVO> getSubCatList(Integer rootCatId);

    /**
     * 查询首页每个一节分类下的6条最新商品数据
     */
    public List<NewItemsVo> getSixNewItemsLazy(Integer rootCatId);



}
