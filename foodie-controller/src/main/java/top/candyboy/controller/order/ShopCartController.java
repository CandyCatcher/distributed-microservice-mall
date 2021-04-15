package top.candyboy.controller.order;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.candyboy.controller.BaseController;
import top.candyboy.order.pojo.bo.ShopCartBO;
import top.candyboy.utils.IMOOCJSONResult;
import top.candyboy.utils.JsonUtils;
import top.candyboy.utils.RedisOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

//import static top.candyboy.web.controller.BaseController.FOODIE_SHOPCART;

@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RestController
@RequestMapping("shopcart")
public class ShopCartController {

    final static Logger logger = LoggerFactory.getLogger("ShopCartController");

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedisOperator redisOperator;

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @RequestMapping("/add")
    public IMOOCJSONResult add(@RequestParam String userId,
                               @RequestBody ShopCartBO shopCartBO,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        //System.out.println(shopCartBO);

        /*
        前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存，
        这时需要判断当前购物车中包含已经存在的商品，如果存在则累加商品数量
         */
        // 加冒号是为了在rdm中好看
        // 需要加上userId进行判断是谁的购物车呀
        String shopCartStr = redisOperator.get(BaseController.FOODIE_SHOPCART + ":" + userId);
        List<ShopCartBO> shopCartBOList = null;
        if (StringUtils.isBlank(shopCartStr)) {
            // redis中没有购物车，直接添加到购物车中
            shopCartBOList = new ArrayList<>();
            shopCartBOList.add(shopCartBO);
            //redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopCartBOList);
        } else {
            // redis缓存中已经有购物车了
            shopCartBOList = JsonUtils.jsonToList(shopCartStr, ShopCartBO.class);
            // 判断购物车是否存在已有商品，如果有的话，将值增加？
            boolean isHaving = false;
            for (ShopCartBO sc : shopCartBOList) {
                // 购物车中是以商品规格存储的，所以这里获取商品规格
                String tempSpecId = sc.getSpecId();
                // shopCartBO是前端传过来的数据
                if (tempSpecId.equals(shopCartBO.getSpecId())) {
                    sc.setBuyCounts(sc.getBuyCounts() + shopCartBO.getBuyCounts());
                    isHaving = true;
                }
            }
            // 如果没有直接添加
            if (!isHaving) {
                shopCartBOList.add(shopCartBO);
            }
        }
        // 前面的操作没有涉及到缓存，这里覆盖redis中的缓存
        redisOperator.set(BaseController.FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopCartBOList));
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "删除购物车里的商品", notes = "删除购物车里的商品", httpMethod = "POST")
    @RequestMapping("/del")
    public IMOOCJSONResult add(@RequestParam String userId,
                               @RequestParam String itemSpecId,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return IMOOCJSONResult.errorMap("参数不能为空");
        }

        // 前端用户在登录的情况下，删除购物车里的商品，会同时在后端redis缓存中同步删除购物车
        String shopCartStr = redisOperator.get(BaseController.FOODIE_SHOPCART + ":" + userId);
        List<ShopCartBO> shopCartBOList = null;
        if (StringUtils.isBlank(shopCartStr)) {
            // redis缓存中已经有购物车了
            shopCartBOList = JsonUtils.jsonToList(shopCartStr, ShopCartBO.class);
            // 判断购物车是否存在已有商品，如果有的话，直接删除
            for (ShopCartBO sc : shopCartBOList) {
                // 购物车中是以商品规格存储的，所以这里获取商品规格
                String tempSpecId = sc.getSpecId();
                // shopCartBO是前端传过来的数据
                if (tempSpecId.equals(itemSpecId)) {
                    shopCartBOList.remove(sc);
                    // break;???
                }
            }
        }
        // 前面的操作没有涉及到缓存，这里覆盖redis中的缓存
        redisOperator.set(BaseController.FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopCartBOList));
        return IMOOCJSONResult.ok();
    }

}
