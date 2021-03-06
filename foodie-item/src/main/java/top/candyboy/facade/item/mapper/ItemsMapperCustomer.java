package top.candyboy.facade.item.mapper;

import org.apache.ibatis.annotations.Param;
import top.candyboy.pojo.item.vo.ItemCommentVO;
import top.candyboy.pojo.item.vo.SearchItemsVO;
import top.candyboy.pojo.item.vo.ShopCartVO;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustomer {

    public List<ItemCommentVO> getItemsMapper(@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemsVO> searchItems(@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemsVO> searchItemsByThirdCat(@Param("paramsMap") Map<String, Object> map);

    public List<ShopCartVO> queryItemBySpecId(@Param("paramsList") List<Object> list);

    public int decreaseItemSpecStock(@Param("specId") String specId, @Param("pendingCounts") int pendingCounts);

}
