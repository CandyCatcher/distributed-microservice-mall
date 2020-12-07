package top.candysky.service;

import top.candysky.pojo.*;
import top.candysky.pojo.vo.CommentLevelCountsVO;
import top.candysky.pojo.vo.ShopCartVO;
import top.candysky.utils.PagedGridResult;

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
}
