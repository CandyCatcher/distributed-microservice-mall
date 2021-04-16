package top.candyboy.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import top.candyboy.constant.Constant;
import top.candyboy.facade.center.MyOrdersService;
import top.candyboy.pojo.order.Orders;
import top.candyboy.pojo.user.Users;
import top.candyboy.pojo.user.vo.UsersVO;
import top.candyboy.utils.IMOOCJSONResult;
import top.candyboy.redis.RedisOperator;

import java.util.UUID;

//@Controller
@RestController
public class BaseController {

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
        redisOperator.set(Constant.REDIS_USER_TOKEN + ":" + userResult.getId(), uniqueToken);
        // 将user和token放在一起，放到cookie中
        UsersVO usersVO = new UsersVO();
        // 多余出来的就不会拷贝进去
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }
}
