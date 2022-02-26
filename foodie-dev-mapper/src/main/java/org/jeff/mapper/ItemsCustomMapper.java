package org.jeff.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeff.pojo.vo.ItemCommentVO;
import org.jeff.pojo.vo.SearchItemVO;
import org.jeff.pojo.vo.ShopcartVO;

import java.util.List;
import java.util.Map;

public interface ItemsCustomMapper {
    public List<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemVO> searchItem(@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemVO> searchItemByThirdCat(@Param("paramsMap") Map<String, Object> map);

    public List<ShopcartVO> queryItemBySpecIds(@Param("paramsList") List specIdsList);

    public int decreaseItemSpecStock(@Param("specId")String specId, @Param("pendingCounts") int pendingCounts);
}