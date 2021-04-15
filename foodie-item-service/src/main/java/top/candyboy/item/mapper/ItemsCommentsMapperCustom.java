package top.candyboy.item.mapper;

import org.apache.ibatis.annotations.Param;
import top.candyboy.item.pojo.ItemsComments;
import top.candyboy.item.pojo.vo.MyCommentVO;

import java.util.List;
import java.util.Map;

public interface ItemsCommentsMapperCustom extends MyMapper<ItemsComments> {

    public void saveComments(Map<String, Object> map);

    public List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String, Object> map);

}