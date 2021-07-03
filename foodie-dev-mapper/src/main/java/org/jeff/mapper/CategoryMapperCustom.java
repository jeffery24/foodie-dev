package org.jeff.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeff.pojo.vo.CategoryVO;
import org.jeff.pojo.vo.NewItemsVo;

import java.util.List;
import java.util.Map;

public interface CategoryMapperCustom {

    public List<CategoryVO> getSubCatList(Integer rootCatId);

    public List<NewItemsVo> getNewSixCatList(@Param("paramsMap") Map<String,Object> map);

}