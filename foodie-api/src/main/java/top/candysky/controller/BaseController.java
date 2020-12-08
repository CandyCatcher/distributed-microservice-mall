package top.candysky.controller;

import org.springframework.web.bind.annotation.RestController;

//@Controller
@RestController
public class BaseController {
    public static final Integer COMMON_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;

    // 微信支付成功后->支付中心->天天吃货平台的回调通知URL
    public static final String PAYRETURNURL = "http://localhost:8080/orders/notifynotifyMerchantOrderPaid";
}
