package top.candysky.mapper;

import top.candysky.my.mapper.MyMapper;
import top.candysky.pojo.ItemsComments;
import top.candysky.pojo.vo.MyCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsCommentsMapperCustom extends MyMapper<ItemsComments> {

    public void saveComments(Map<String, Object> map);

    public List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String, Object> map);

}