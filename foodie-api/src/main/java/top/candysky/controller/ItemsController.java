package top.candysky.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.candysky.enums.YesOrNo;
import top.candysky.pojo.*;
import top.candysky.pojo.vo.CategoryVO;
import top.candysky.pojo.vo.CommentLevelCountsVO;
import top.candysky.pojo.vo.ItemInfoVO;
import top.candysky.pojo.vo.NewItemsVO;
import top.candysky.service.CarouselService;
import top.candysky.service.CategoryService;
import top.candysky.service.ItemService;
import top.candysky.utils.IMOOCJSONResult;
import top.candysky.utils.PagedGridResult;

import java.util.List;

import static top.candysky.controller.BaseController.COMMON_PAGE_SIZE;
import static top.candysky.controller.BaseController.PAGE_SIZE;

@Api(value = "商品接口", tags = {"商品相关信息的接口"})
//@Controller
@RestController
@RequestMapping("items")
public class ItemsController {

    final static Logger logger = LoggerFactory.getLogger("ItemsController");

    @Autowired
    private ItemService itemsService;

    @ApiOperation(value = "获取商品页面的详细信息", notes = "点击首页商品展示图片，跳转到详情页", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public IMOOCJSONResult getItemInfo(
            @ApiParam(value = "商品id", name = "itemId", required = true)
            @PathVariable String itemId) {

        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        Items item = itemsService.queryItemId(itemId);
        List<ItemsImg> itemImgList = itemsService.queryItemImgList(itemId);
        List<ItemsSpec> itemSpecList = itemsService.queryItemSpecList(itemId);
        ItemsParam itemParams = itemsService.queryItemParam(itemId);
        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(item);
        itemInfoVO.setItemImgList(itemImgList);
        itemInfoVO.setItemSpecList(itemSpecList);
        itemInfoVO.setItemParams(itemParams);
        return IMOOCJSONResult.ok(itemInfoVO);
    }

    @ApiOperation(value = "获取商品评价等级数量", notes = "点击首页商品展示图片，跳转到详情页", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public IMOOCJSONResult getCommentsCount(
            @ApiParam(value = "商品id", name = "itemId", required = true)
            @RequestParam String itemId) {

        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        CommentLevelCountsVO countsVO = itemsService.queryCommentCounts(itemId);
        return IMOOCJSONResult.ok(countsVO);
    }

    @ApiOperation(value = "获取商品评价等级数量", notes = "点击首页商品展示图片，跳转到详情页", httpMethod = "GET")
    @GetMapping("/comments")
    public IMOOCJSONResult getCommentsCount(
            @ApiParam(value = "商品id", name = "itemId", required = true)
            @RequestParam String itemId,
            @ApiParam(value = "评价等级", name = "level", required = true)
            @RequestParam Integer level,
            @ApiParam(value = "查询下一页的第几页", name = "page", required = true)
            @RequestParam Integer page,
            @ApiParam(value = "分页的每一页显示的条数", name = "pageSize", required = true)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = itemsService.queryCommentsByPage(itemId, level, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }

    @ApiOperation(value = "获取商品评价等级数量", notes = "点击首页商品展示图片，跳转到详情页", httpMethod = "GET")
    @GetMapping("/search")
    public IMOOCJSONResult search(
            @ApiParam(name = "keywords", value = "关键字", required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedGridResult grid = itemsService.searchItemsBykeywords(keywords, sort, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }

    @ApiOperation(value = "获取商品评价等级数量", notes = "点击首页商品展示图片，跳转到详情页", httpMethod = "GET")
    @GetMapping("/catItems")
    public IMOOCJSONResult catItems(
            @ApiParam(name = "catId", value = "三级分类id", required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (catId == null) {
            return IMOOCJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedGridResult grid = itemsService.searchItemsByThirdCat(catId, sort, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }
}
