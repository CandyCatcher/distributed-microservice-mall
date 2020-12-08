package top.candysky.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import top.candysky.enums.OrderStatusEnum;
import top.candysky.enums.PayMethod;
import top.candysky.pojo.bo.SubmitOrderBO;
import top.candysky.pojo.vo.MerchantOrdersVO;
import top.candysky.pojo.vo.OrderVO;
import top.candysky.service.ItemService;
import top.candysky.service.OrderService;
import top.candysky.utils.IMOOCJSONResult;

import static top.candysky.controller.BaseController.PAYMENTURL;

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

    @ApiOperation(value = "获取商品页面的详细信息", notes = "点击首页商品展示图片，跳转到详情页", httpMethod = "GET")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO) {
        if (!submitOrderBO.getPayMethod().equals(PayMethod.WECHAT.value) && !submitOrderBO.getPayMethod().equals(PayMethod.ALIPAY.value)) {
            return IMOOCJSONResult.errorMsg("支付方式不支持");
        }
        /*
         * 1.创建订单
         * 2.创建订单之后，移除购物车中已结算（已提交）的商品
         * 3.向支付中心发送当前订单，用于保存支付中心的订单数据
         */
        OrderVO orderVO = orderService.createOrder(submitOrderBO);
        String orderId = orderVO.getOrderId();

        // TODO 整合redis之后，完善购物车的已结算商品清楚，并且同步到前端的cookie

        // 将商户订单的信息发送给支付中心，用于保存支付中心的订单数据
        // 那怎么在我们的系统中去调用另一个系统的功能呢？
        // 可以用http，也可以用spring的rest
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(BaseController.PAYRETURNURL);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId", "imooc");
        headers.add("password", "imooc");

        // 传过去的对象类型
        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO, headers);

        // 返回的类型IMOOCJSONResult.class
        ResponseEntity<IMOOCJSONResult> responseEntity = restTemplate.postForEntity(PAYMENTURL, entity, IMOOCJSONResult.class);

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
