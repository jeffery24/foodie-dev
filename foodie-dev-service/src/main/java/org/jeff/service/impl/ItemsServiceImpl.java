package org.jeff.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.jeff.enums.CommentLevel;
import org.jeff.enums.YesOrNo;
import org.jeff.mapper.*;
import org.jeff.pojo.*;
import org.jeff.pojo.vo.CommentVO;
import org.jeff.pojo.vo.ItemCommentVO;
import org.jeff.pojo.vo.SearchItemVO;
import org.jeff.pojo.vo.ShopcartVO;
import org.jeff.service.ItemsService;
import org.jeff.util.DesensitizationUtil;
import org.jeff.util.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

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
    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;
    @Autowired
    private ItemsCustomMapper itemsCustomMapper;

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
        imgExpCriteria.andEqualTo("itemId", itemId);

        return itemsImgMapper.selectByExample(itemImgExp);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example itemSpecExp = new Example(ItemsSpec.class);
        Example.Criteria imgExpCriteria = itemSpecExp.createCriteria();
        imgExpCriteria.andEqualTo("itemId", itemId);

        return itemsSpecMapper.selectByExample(itemSpecExp);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParamById(String itemId) {
        Example itemParamExp = new Example(ItemsParam.class);
        Example.Criteria imgExpCriteria = itemParamExp.createCriteria();
        imgExpCriteria.andEqualTo("itemId", itemId);

        return itemsParamMapper.selectOneByExample(itemParamExp);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentVO queryCommentCounts(String itemId) {

        int goodCounts = getCommentCounts(itemId, CommentLevel.GOOD.type);
        int normalCounts = getCommentCounts(itemId, CommentLevel.NORMAL.type);
        int badCounts = getCommentCounts(itemId, CommentLevel.BAD.type);
        int totalCounts = goodCounts + normalCounts + badCounts;

        CommentVO commentVO = new CommentVO();
        commentVO.setTotalCounts(totalCounts);
        commentVO.setGoodCounts(goodCounts);
        commentVO.setNormalCounts(normalCounts);
        commentVO.setBadCounts(badCounts);

        return commentVO;
    }

    Integer getCommentCounts(String itemId, Integer level) {
        ItemsComments itemsComments = new ItemsComments();
        itemsComments.setItemId(itemId);
        if (level != null) {
            itemsComments.setCommentLevel(level);
        }
        return itemsCommentsMapper.selectCount(itemsComments);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryPageComments(String itemId, Integer level, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("level", level);
        /**
         * page: 第几页
         * pageSize: 每页显示条数
         */
        PageHelper.startPage(page, pageSize);
        List<ItemCommentVO> list = itemsCustomMapper.queryItemComments(map);

        // 用户名脱敏
        for (ItemCommentVO vo : list) {
            vo.setNickname(DesensitizationUtil.commonDisplay(vo.getNickname()));
        }

        return setPage(list, page);
    }

    public PagedGridResult setPage(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);

        PageHelper.startPage(page, pageSize);
        List<SearchItemVO> list = itemsCustomMapper.searchItem(map);

        return setPage(list, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);

        PageHelper.startPage(page, pageSize);
        List<SearchItemVO> list = itemsCustomMapper.searchItemByThirdCat(map);

        return setPage(list, page);
    }

    @Override
    public List<ShopcartVO> queryItemBySpecIds(String specIds) {
        String[] split = specIds.split(",");
        List<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList, split);

        return itemsCustomMapper.queryItemBySpecIds(specIdsList);
    }

    @Override
    public ItemsSpec queryItemSpec(String specId) {
        return itemsSpecMapper.selectByPrimaryKey(specId);
    }


    @Override
    public String queryItemMainImgById(String itemId) {
        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNo.YES.type);
        ItemsImg result = itemsImgMapper.selectOne(itemsImg);
        return result != null ? result.getUrl() : " ";
    }

    @Override
    public void decreaseItemSpecStock(String itemSpecId, int buyCounts) {
        // synchronized 不推荐使用,集群下无用,效率低下
        // 锁数据库：不推荐,导致数据库性能低下
        // 分布式锁: Zookeeper、Redis

        // lockUtil.getLock(); -- 加锁
        //1.查询库存
        //    int stock = 10;

        //2.判断库存,是否能够减少到0以下

        //if (stock - buyCounts >0){
        // 提示用户库存不够
//            10 - 3 -3 - 5 = -1
//        }
        // lockUtil.unLock(); -- 解锁
        int result = itemsCustomMapper.decreaseItemSpecStock(itemSpecId, buyCounts);
        if (result != 1) {
            throw new RuntimeException("订单创建失败，原因：库存不足!");
        }
    }
}
