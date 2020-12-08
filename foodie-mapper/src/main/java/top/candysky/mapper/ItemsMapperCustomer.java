package top.candysky.mapper;

import org.apache.ibatis.annotations.Param;
import top.candysky.my.mapper.MyMapper;
import top.candysky.pojo.ItemsComments;
import top.candysky.pojo.vo.ItemCommentVO;
import top.candysky.pojo.vo.SearchItemsVO;
import top.candysky.pojo.vo.ShopCartVO;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustomer {

    public List<ItemCommentVO> getItemsMapper(@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemsVO> searchItems(@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemsVO> searchItemsByThirdCat(@Param("paramsMap") Map<String, Object> map);

    public List<ShopCartVO> queryItemBySpecId(@Param("paramsList") List<Object> list);

    public int decreaseItemSpecStock(@Param("specId") String specId, @Param("pendingCounts") int pendingCounts);

}
