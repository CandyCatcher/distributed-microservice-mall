package top.candyboy.controller.order;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import top.candyboy.constant.Constant;
import top.candyboy.enums.OrderStatusEnum;
import top.candyboy.enums.PayMethod;
import top.candyboy.facade.order.OrderService;
import top.candyboy.item.ItemService;
import top.candyboy.pojo.order.bo.ShopCartBO;
import top.candyboy.pojo.order.bo.SubmitOrderBO;
import top.candyboy.pojo.order.vo.MerchantOrdersVO;
import top.candyboy.pojo.order.vo.OrderVO;
import top.candyboy.utils.CookieUtils;
import top.candyboy.utils.IMOOCJSONResult;
import top.candyboy.utils.JsonUtils;
import top.candyboy.redis.RedisOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

//import static top.candyboy.web.controller.BaseController.FOODIE_SHOPCART;
//import static top.candyboy.web.controller.BaseController.PAYMENTURL;

@Api(value = "订单相关", tags = {"订单相关的API接口"})
@RestController
@RequestMapping("orders")
public class OrdersController {
    final static Logger logger = LoggerFactory.getLogger("OrdersController");

    @Autowired
    private ItemService itemsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "获取商品页面的详细信息", notes = "点击首页商品展示图片，跳转到详情页", httpMethod = "GET")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        if (!submitOrderBO.getPayMethod().equals(PayMethod.WECHAT.value) && !submitOrderBO.getPayMethod().equals(PayMethod.ALIPAY.value)) {
            return IMOOCJSONResult.errorMsg("支付方式不支持");
        }

        // 在创建订单之前，需要看redis中有没有相应的购物车，
        String shopCartStr = redisOperator.get(Constant.FOODIE_SHOPCART + ":" + submitOrderBO.getUserId());
        if (StringUtils.isBlank(shopCartStr)) {
            // 因为后面要使用到购物车，所以这里购物车不能为空
            return IMOOCJSONResult.errorMsg("购物车数据错误");
        }

        List<ShopCartBO> shopCartList = JsonUtils.jsonToList(shopCartStr, ShopCartBO.class);

        /*
         * 1.创建订单
         * 2.创建订单之后，移除购物车中已结算（已提交）的商品
         * 3.向支付中心发送当前订单，用于保存支付中心的订单数据
         */
        OrderVO orderVO = orderService.createOrder(shopCartList, submitOrderBO);
        String orderId = orderVO.getOrderId();

        /*
         整合redis之后，完善购物车的已结算商品数据，并且同步到前端的cookie
         清理要去除的商品
         覆盖现有的redis汇总的购物数据
         */
        shopCartList.removeAll(orderVO.getToBeRemovedShopcartList());
        redisOperator.set(Constant.FOODIE_SHOPCART + ":" + submitOrderBO.getUserId(), JsonUtils.objectToJson(shopCartList));
        CookieUtils.setCookie(request, response, Constant.FOODIE_SHOPCART, "");

        // 将商户订单的信息发送给支付中心，用于保存支付中心的订单数据
        // 那怎么在我们的系统中去调用另一个系统的功能呢？
        // 可以用http，也可以用spring的rest
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(Constant.PAYRETURNURL);

        merchantOrdersVO.setAmount(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId", "imooc");
        headers.add("password", "imooc");

        // 传过去的对象类型
        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO, headers);

        // 返回的类型IMOOCJSONResult.class
        ResponseEntity<IMOOCJSONResult> responseEntity = restTemplate.postForEntity(Constant.PAYMENTURL, entity, IMOOCJSONResult.class);

        IMOOCJSONResult payResult = responseEntity.getBody();

        if (payResult.getStatus() != 200) {
            return IMOOCJSONResult.errorMsg("支付中心订单创建失败");
        }

        return IMOOCJSONResult.ok(orderId);
    }

    /*
    商户端成功支付的返回结果
     */
    @PostMapping("/notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.value);
        return HttpStatus.OK.value();
    }


}
