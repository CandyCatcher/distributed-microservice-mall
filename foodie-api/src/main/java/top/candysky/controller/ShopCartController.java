package top.candysky.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import top.candysky.pojo.bo.ShopCartBO;
import top.candysky.utils.IMOOCJSONResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RestController
@RequestMapping("shopcart")
public class ShopCartController {

    final static Logger logger = LoggerFactory.getLogger("ShopCartController");

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @RequestMapping("/add")
    public IMOOCJSONResult add(@RequestParam String userId,
                               @RequestBody ShopCartBO shopCartBO,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        System.out.println(shopCartBO);

        // TODO 前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
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

        // TODO 前端用户在登录的情况下，删除购物车里的商品，会同时在后端redis缓存中同步删除购物车
        return IMOOCJSONResult.ok();
    }

}
