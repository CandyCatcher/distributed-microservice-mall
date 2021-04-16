package top.candyboy.controller.item;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.candyboy.enums.YesOrNo;
import top.candyboy.item.CarouselService;
import top.candyboy.item.CategoryService;
import top.candyboy.pojo.item.Carousel;
import top.candyboy.pojo.item.Category;
import top.candyboy.pojo.item.vo.CategoryVO;
import top.candyboy.pojo.item.vo.NewItemsVO;
import top.candyboy.utils.IMOOCJSONResult;
import top.candyboy.utils.JsonUtils;
import top.candyboy.redis.RedisOperator;

import java.util.ArrayList;
import java.util.List;

@Api(value = "首页", tags = {"首页相关接口"})
//@Controller
@RestController
@RequestMapping("index")
public class IndexController {

    final static Logger logger = LoggerFactory.getLogger("IndexController");

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisOperator redisOperator;


    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public IMOOCJSONResult carousel() {
        /*
        1.redis缓存中是否有
        2.  没有的话添加
            有的话直接获取
         */
        List<Carousel> carouselList = new ArrayList<>();
        String carouselStr = redisOperator.get("carousel");
        if (StringUtils.isBlank(carouselStr)) {
            carouselList = carouselService.queryAll(YesOrNo.YES.type);
            redisOperator.set("carousel", JsonUtils.objectToJson(carouselList));
        } else {
            carouselList = JsonUtils.jsonToList(carouselStr, Carousel.class);
        }
        return IMOOCJSONResult.ok(carouselList);
    }

    /*
    1.后台运营系统，一旦广告（轮播图）发生更改，就可以删除缓存，然后重置
    2.定时重置
    3.每个轮播图都有可能是个广告，每个广告都有一个过期时间，过期了再重置
     */

    /**
     * 首页分类展示:
     * 1.第一次刷新主页查询大分类，渲染展示到首页
     * 2.如果鼠标上移到大分类，则加载其子分类的内容，如果已经存在子分类，则不需要加载（懒加载）
     */
    @ApiOperation(value = "获取商品分类（一级分类）", notes = "获取商品分类（一级分类）", httpMethod = "GET")
    @GetMapping("/cats")
    public IMOOCJSONResult cats() {
        List<Category> categoryList = new ArrayList<>();
        String categoryStr = redisOperator.get("category");
        if (StringUtils.isBlank(categoryStr)) {
            categoryList = categoryService.queryAllRootLevelCat();
            redisOperator.set("categoryList", JsonUtils.objectToJson(categoryList));
        } else {
            categoryList = JsonUtils.jsonToList(categoryStr, Category.class);
        }
        return IMOOCJSONResult.ok(categoryList);
    }

    @ApiOperation(value = "获取商品分类（二级分类）", notes = "通过商品的一级分类ID获取商品分类（二级分类）", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public IMOOCJSONResult subCat(
            @ApiParam(name = "rootCatId", value = "一级分类Id", required = true)
            @PathVariable Integer rootCatId) {
        if (rootCatId == null) {
            return IMOOCJSONResult.errorMsg("分类不存在");
        }
        List<CategoryVO> subCatList = new ArrayList<>();
        String subCategoryStr = redisOperator.get("subCategory");
        if (StringUtils.isBlank(subCategoryStr)) {
            subCatList = categoryService.getSubCatList(rootCatId);
            redisOperator.set("subCategory", JsonUtils.objectToJson(subCatList));
        } else {
            subCatList = JsonUtils.jsonToList(subCategoryStr, CategoryVO.class);
        }
        return IMOOCJSONResult.ok(subCatList);
    }

    //@ApiOperation(value = "获取商品分类（三级分类）", notes = "获取商品分类（三级分类）", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public IMOOCJSONResult sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类Id", required = true)
            @PathVariable Integer rootCatId) {
        if (rootCatId == null) {
            return IMOOCJSONResult.errorMsg("分类不存在");
        }

        List<NewItemsVO> newItemsVOList = new ArrayList<>();
        String newItemsStr = redisOperator.get("newItems");
        if (StringUtils.isBlank(newItemsStr)) {
            newItemsVOList = categoryService.getSixNewItems(rootCatId);
            redisOperator.set("newItems", JsonUtils.objectToJson(newItemsVOList));
        } else {
            newItemsVOList = JsonUtils.jsonToList(newItemsStr, NewItemsVO.class);
        }
        return IMOOCJSONResult.ok(newItemsVOList);
    }


}
