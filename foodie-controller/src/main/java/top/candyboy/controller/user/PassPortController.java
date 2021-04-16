package top.candyboy.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.candyboy.constant.Constant;
import top.candyboy.controller.BaseController;
import top.candyboy.facade.user.UserService;
import top.candyboy.pojo.order.bo.ShopCartBO;
import top.candyboy.pojo.user.Users;
import top.candyboy.pojo.user.bo.UserBO;
import top.candyboy.pojo.user.vo.UsersVO;
import top.candyboy.redis.RedisOperator;
import top.candyboy.utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

// 这个接口可以添加一些内容
@Api(value = "注册&登录", tags = {"用于注册和登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassPortController extends BaseController {

    final static Logger logger = LoggerFactory.getLogger("PassPortController");

    @Autowired
    UserService userService;

    @Autowired
    RedisOperator redisOperator;

    // 用于阐述当前方法的内容
    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    // @RequestParam说明是请求的参数不是什么url值
    public IMOOCJSONResult usernameIsExist(@RequestParam String username) {

        // 判空
        if (StringUtils.isBlank(username)) {
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }

        // 判断是否存在
        if (userService.queryUsernameIsExist(username)) {
            return IMOOCJSONResult.errorMap("用户名已存在");
        }

        return IMOOCJSONResult.ok();
    }

    @PostMapping("/regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        // 1.判断用户名和密码都不能为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)) {
            return IMOOCJSONResult.errorMsg("用户名和密码都不能为空");
        }

        // 2.查询用户名是否存在
        if (userService.queryUsernameIsExist(username)) {
            return IMOOCJSONResult.errorMsg("用户名已存在");
        }

        // 3.判断两个密码是否一样
        if (!password.equals(confirmPassword)) {
            return IMOOCJSONResult.errorMsg("输入的两次密码不一致");
        }

        // 4.密码长度不能少于6位
        if (password.length() < 6) {
            return IMOOCJSONResult.errorMsg("密码长度小于6位");
        }

        // 5. 实现注册
        Users userResult = userService.createUser(userBO);
        // 降不必要的内容忽略掉
        //userResult = setNullProperty(userResult);

        UsersVO usersVO = convertUsersVO(userResult);

        // 注册完cookie信息会被覆盖
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户名登录", httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        // 1.判断用户名和密码都不能为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return IMOOCJSONResult.errorMsg("用户名和密码都不能为空");
        }
        // 2. 实现登录
        Users userResult = null;
        try {
            userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userResult == null) {
            return IMOOCJSONResult.errorMsg("用户名或密码错误");
        }

         /*
        生成用户token，存入redis会话
         */
        UsersVO usersVO = convertUsersVO(userResult);

        //userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);

        /*
        同步购物车数据
         */
        synchShopCartData(userResult.getId(), request, response);

        return IMOOCJSONResult.ok(userResult);
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId, HttpServletRequest request, HttpServletResponse response) {

        /*
        以前的话，如果用户要退出登录，是要清除session中的登录信息，在这里就不需要做，我们只是使用了一个cookie
         */
        CookieUtils.deleteCookie(request, response, "user");

        /*
        用户退出登录, 前端的cookie需要清掉，redis中的一些信息也需要清理
         */
        redisOperator.del(Constant.REDIS_USER_TOKEN + ":" + userId);
        // 分布式会话需要清除用户数据
        CookieUtils.deleteCookie(request, response, Constant.FOODIE_SHOPCART);

        return IMOOCJSONResult.ok();
    }

    /**
     * 注册登录成功后，同步cookie和redis的购物车
     */
    private void synchShopCartData(String userId, HttpServletRequest request, HttpServletResponse response) {
        /*
        1.redis中没有数据，如果cookie中的购物车为空，那么这个时候不作任何处理
                         如果cookie中的购物车不为空，那么将cookie的数据直接放在redis中
        2.redis中有数据，如果cookie中的购物车为空，那么直接把redis中的购物车覆盖本地cookie
                        如果cookie中的购物车不为空，同时cookie中的某个商品在redis中存在，则以cookie为主，删除redis中的，
                        把cookie中的商品直接覆盖redis中
        3.同步到redis中之后，覆盖本地cookie购物车的数据，保证本地购物车的数据是同步到最新的
         */
        // 从redis中获取购物车
        String shopCartJsonRedis = redisOperator.get(Constant.FOODIE_SHOPCART + ":" + userId);

        // 从cookie中获取购物车
        String shopCartJsonCookie = CookieUtils.getCookieValue(request, response, Constant.FOODIE_SHOPCART);

        if (StringUtils.isBlank(shopCartJsonRedis)) {
            if (StringUtils.isNotBlank(shopCartJsonCookie)) {
                redisOperator.set(Constant.FOODIE_SHOPCART + ":" + userId, shopCartJsonCookie);
            }
        } else {
            if (StringUtils.isBlank(shopCartJsonCookie)) {
                CookieUtils.setCookie(request, response, Constant.FOODIE_SHOPCART, shopCartJsonRedis, true);
            } else {
                /*
                 1. 已经存在的，把cookie中对应的数量，覆盖redis（参考京东）
                 2. 该项商品标记为待删除，统一放入一个待删除的list
                 3. 从cookie中清理所有的待删除list
                 4. 合并redis和cookie中的数据
                 5. 更新到redis和cookie中
                 */
                List<ShopCartBO> shopCartBOListRedis = JsonUtils.jsonToList(shopCartJsonRedis, ShopCartBO.class);
                List<ShopCartBO> shopCartBOListCookie = JsonUtils.jsonToList(shopCartJsonCookie, ShopCartBO.class);

                // 定义一个待删除的list
                List<ShopCartBO> pendingDeleteList = new ArrayList<>();

                for (ShopCartBO redisShopCart : shopCartBOListRedis) {
                    String redisSpecId = redisShopCart.getSpecId();

                    for (ShopCartBO cookieShopCart : shopCartBOListCookie) {
                        String cookieSpecId = cookieShopCart.getSpecId();

                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖redis的购买数量，不累加
                            redisShopCart.setBuyCounts(cookieShopCart.getBuyCounts());
                            // 把cookieShopCart放入待删除的列表中，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopCart);
                        }
                    }
                }

                // 从现有的cookie中删除对应的覆盖过的商品数据
                shopCartBOListCookie.removeAll(pendingDeleteList);
                // 合并两个list
                // TODO 这样就合并了？
                shopCartBOListRedis.addAll(shopCartBOListCookie);
                // 更新到redis和cookie
                CookieUtils.setCookie(request, response, Constant.FOODIE_SHOPCART, JsonUtils.objectToJson(shopCartBOListRedis), true);
                redisOperator.set(Constant.FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopCartBOListRedis));
            }
        }
    }

    //private Users setNullProperty(Users userResult) {
    //
    //    userResult.setPassword(null);
    //    userResult.setRealname(null);
    //    userResult.setMobile(null);
    //    userResult.setMobile(null);
    //    userResult.setEmail(null);
    //    userResult.setBirthday(null);
    //    userResult.setCreatedTime(null);
    //    userResult.setUpdatedTime(null);
    //
    //    return userResult;
    //}

}
