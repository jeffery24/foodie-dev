package org.jeff.service.impl;

import org.jeff.mapper.ItemsImgMapper;
import org.jeff.mapper.ItemsMapper;
import org.jeff.mapper.ItemsParamMapper;
import org.jeff.mapper.ItemsSpecMapper;
import org.jeff.pojo.Items;
import org.jeff.pojo.ItemsImg;
import org.jeff.pojo.ItemsParam;
import org.jeff.pojo.ItemsSpec;
import org.jeff.service.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ItemsServiceImpl implements ItemsService {

    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private ItemsImgMapper itemsImgMapper;
    @Autowired
    private ItemsSpecMapper itemsSpecMapper;
    @Autowired
    private ItemsParamMapper itemsParamMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {
        Example itemImgExp = new Example(ItemsImg.class);
        Example.Criteria imgExpCriteria = itemImgExp.createCriteria();
        imgExpCriteria.andEqualTo("itemId",itemId);

        return itemsImgMapper.selectByExample(itemImgExp);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example itemSpecExp = new Example(ItemsSpec.class);
        Example.Criteria imgExpCriteria = itemSpecExp.createCriteria();
        imgExpCriteria.andEqualTo("itemId",itemId);

        return itemsSpecMapper.selectByExample(itemSpecExp);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParamById(String itemId) {
        Example itemParamExp = new Example(ItemsParam.class);
        Example.Criteria imgExpCriteria = itemParamExp.createCriteria();
        imgExpCriteria.andEqualTo("itemId",itemId);

        return itemsParamMapper.selectOneByExample(itemParamExp);
    }
}
