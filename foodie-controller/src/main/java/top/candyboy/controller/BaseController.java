package top.candyboy.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import top.candyboy.center.service.MyOrdersService;
import top.candyboy.order.pojo.Orders;
import top.candyboy.user.pojo.Users;
import top.candyboy.user.pojo.vo.UsersVO;
import top.candyboy.utils.IMOOCJSONResult;
import top.candyboy.utils.RedisOperator;

import java.io.File;
import java.util.UUID;

//@Controller
@RestController
public class BaseController {
    public static final String FOODIE_SHOPCART = "shopcart";
    public static final String REDIS_USER_TOKEN = "redis_user_token";

    public static final Integer COMMON_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;

    // 支付中心的调用地址
    public static final String PAYMENTURL = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

    // 微信支付成功后->支付中心->天天吃货平台的回调通知URL
    public static final String PAYRETURNURL = "http://localhost:8080/orders/notifynotifyMerchantOrderPaid";


    // 用户上传头像的位置
    public static final String IMAGE_USER_FACE_LOCATION = File.separator + "workspaces" +
            File.separator + "images" +
            File.separator + "foodie" +
            File.separator + "faces";

    @Autowired
    public MyOrdersService myOrdersService;

    @Autowired
    public RedisOperator redisOperator;

    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     * @return
     */
    public IMOOCJSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return IMOOCJSONResult.errorMsg("订单不存在！");
        }
        return IMOOCJSONResult.ok(order);
    }

    public UsersVO convertUsersVO(Users userResult) {
         /*
        session会话其实就是设置了用户登录的状态
        实现用户的redis会话
        生成token，存到缓存中
         */
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(), uniqueToken);
        // 将user和token放在一起，放到cookie中
        UsersVO usersVO = new UsersVO();
        // 多余出来的就不会拷贝进去
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }
}
