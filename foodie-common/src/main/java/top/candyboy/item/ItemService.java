package top.candyboy.item;

import top.candyboy.pojo.item.Items;
import top.candyboy.pojo.item.ItemsImg;
import top.candyboy.pojo.item.ItemsParam;
import top.candyboy.pojo.item.ItemsSpec;
import top.candyboy.pojo.item.vo.CommentLevelCountsVO;
import top.candyboy.pojo.item.vo.ShopCartVO;
import top.candyboy.utils.PagedGridResult;

import java.util.List;

public interface ItemService {

    //    根据商品主键查询商品
    public Items queryItemId(String id);

    //    根据商品Id查询商品图片列表
    public List<ItemsImg> queryItemImgList(String id);

    // 根据商品ID查询商品规格信息
    public List<ItemsSpec> queryItemSpecList(String id);

    // 根据商品ID查询商品参数
    public ItemsParam queryItemParam(String id);

    // 根据商品的Id查询商品评价
    public CommentLevelCountsVO queryCommentCounts(String id);

    // 根据商品的Id查询具体
    public PagedGridResult queryCommentsByPage(String id, Integer level, Integer page, Integer pageSize);

    // 根据关键字查询商品
    public PagedGridResult searchItemsBykeywords(String keywords, String sort, Integer page, Integer pageSize);

    public PagedGridResult searchItemsByThirdCat(Integer catId, String sort, Integer page, Integer pageSize);

    public List<ShopCartVO> queryItemBySpecId(String specId);

    public ItemsSpec queryItemSpecById(String itemSpecId);

    String queryItemMainImagById(String itemId);

    void decreaseItemSpecStock(ItemsSpec itemsSpec, Integer buyCounts);
}
