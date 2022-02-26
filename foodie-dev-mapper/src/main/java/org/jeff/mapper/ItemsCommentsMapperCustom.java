package org.jeff.mapper;

import org.jeff.my.mapper.MyMapper;
import org.jeff.pojo.ItemsComments;
import org.jeff.pojo.vo.MyCommentVO;

import java.util.List;
import java.util.Map;

public interface ItemsCommentsMapperCustom extends MyMapper<ItemsComments> {

    public void saveComments(Map<String,Object> map);

    public List<MyCommentVO> queryMyComments(Map<String,Object> map);
}