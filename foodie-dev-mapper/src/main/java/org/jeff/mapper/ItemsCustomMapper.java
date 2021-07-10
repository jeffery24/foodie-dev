package org.jeff.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeff.pojo.vo.ItemCommentVO;
import org.jeff.pojo.vo.SearchItemVO;

import java.util.List;
import java.util.Map;

public interface ItemsCustomMapper{
    public List<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String,Object> map);

    public List<SearchItemVO> searchItem(@Param("paramsMap") Map<String,Object> map);
}