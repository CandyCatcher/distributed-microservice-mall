package top.candyboy.facade.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.candyboy.enums.CommentLevel;
import top.candyboy.enums.YesOrNo;
import top.candyboy.facade.item.ItemService;
import top.candyboy.facade.item.mapper.*;
import top.candyboy.pojo.item.*;
import top.candyboy.pojo.item.vo.CommentLevelCountsVO;
import top.candyboy.pojo.item.vo.ItemCommentVO;
import top.candyboy.pojo.item.vo.SearchItemsVO;
import top.candyboy.pojo.item.vo.ShopCartVO;
import top.candyboy.utils.DesensitizationUtil;
import top.candyboy.utils.PagedGridResult;

import java.util.*;

@DubboService(interfaceClass = ItemService.class)
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private ItemsSpecMapper itemsSpecMapper;

    @Autowired
    private ItemsImgMapper itemsImgMapper;

    @Autowired
    private ItemsParamMapper itemsParamMapper;

    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Autowired
    private ItemsMapperCustomer itemsMapperCustomer;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemId(String id) {
        Example itemExample = new Example(Items.class);
        Example.Criteria criteria = itemExample.createCriteria();
        criteria.andEqualTo("id", id);
        return itemsMapper.selectOneByExample(itemExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgList(String id) {
        Example itemImgExample = new Example(ItemsImg.class);
        Example.Criteria criteria = itemImgExample.createCriteria();
        criteria.andEqualTo("itemId", id);
        return itemsImgMapper.selectByExample(itemImgExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String id) {
        Example itemSpecExample = new Example(ItemsSpec.class);
        Example.Criteria criteria = itemSpecExample.createCriteria();
        criteria.andEqualTo("itemId", id);
        return itemsSpecMapper.selectByExample(itemSpecExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParam(String id) {
        Example itemParamExample = new Example(ItemsParam.class);
        Example.Criteria criteria = itemParamExample.createCriteria();
        criteria.andEqualTo("itemId", id);
        return itemsParamMapper.selectOneByExample(itemParamExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountsVO queryCommentCounts(String id) {
        Integer goodCommentCounts = queryCommentLevelCounts(id, CommentLevel.GOOD.type);
        Integer normalCommentCounts = queryCommentLevelCounts(id, CommentLevel.NORMAL.type);
        Integer badCommentCounts = queryCommentLevelCounts(id, CommentLevel.BAD.type);
        Integer totalCounts = goodCommentCounts + normalCommentCounts + badCommentCounts;
        CommentLevelCountsVO commentLevelCountsVO = new CommentLevelCountsVO();
        commentLevelCountsVO.setGoodCounts(goodCommentCounts);
        commentLevelCountsVO.setNormalCounts(normalCommentCounts);
        commentLevelCountsVO.setBadCounts(badCommentCounts);
        commentLevelCountsVO.setTotalCounts(totalCounts);
        return commentLevelCountsVO;

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer queryCommentLevelCounts(String id, Integer level) {
    //    Example itemCommentExample = new Example(ItemsComments.class);
    //    Example.Criteria criteria = itemCommentExample.createCriteria();
    //    criteria.andEqualTo("itemId", id);
    //    criteria.andEqualTo("commentLevel", level);
    //    return itemsCommentsMapper.selectCountByExample(itemCommentExample);

        ItemsComments condition = new ItemsComments();
        condition.setItemId(id);
        if (level != null) {
            condition.setCommentLevel(level);
        }
        return itemsCommentsMapper.selectCount(condition);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryCommentsByPage(String id, Integer level, Integer page, Integer pageSize) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("itemId", id);
        map.put("level", level);

        // mybatis-pagehelper
        // 这个方法要在查询之前调用

        /**
         * page: 第几页
         * pageSize: 每页显示条数
         */
        PageHelper.startPage(page, pageSize);

        List<ItemCommentVO> itemCommentVOList = itemsMapperCustomer.getItemsMapper(map);

        for (ItemCommentVO itemCommentVO : itemCommentVOList) {
            itemCommentVO.setNickName(DesensitizationUtil.commonDisplay(itemCommentVO.getNickName()));
        }
        return setterPagedGrid(itemCommentVOList, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItemsBykeywords(String keywords, String sort, Integer page, Integer pageSize) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);
        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> searchItemsVOList = itemsMapperCustomer.searchItems(map);
        return setterPagedGrid(searchItemsVOList, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItemsByThirdCat(Integer catId, String sort, Integer page, Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);
        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> searchItemsVOList = itemsMapperCustomer.searchItemsByThirdCat(map);
        return setterPagedGrid(searchItemsVOList, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopCartVO> queryItemBySpecId(String specId) {
        String[] ids = specId.split(",");
        List<Object> list = new ArrayList<>();
        Collections.addAll(list, ids);
        return itemsMapperCustomer.queryItemBySpecId(list);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsSpec queryItemSpecById(String itemSpecId) {
        return itemsSpecMapper.selectByPrimaryKey(itemSpecId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemMainImagById(String itemId) {
        ItemsImg itemImg = new ItemsImg();
        itemImg.setItemId(itemId);
        itemImg.setIsMain(YesOrNo.YES.type);
        return itemsImgMapper.selectOne(itemImg).getUrl();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void decreaseItemSpecStock(ItemsSpec itemsSpec, Integer buyCounts) {
        int result = itemsMapperCustomer.decreaseItemSpecStock(itemsSpec.getId(), buyCounts);
        if (result != 1) {
            throw new RuntimeException("订单创建失败，原因：库存不足！");
        }
    }

    private PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}
