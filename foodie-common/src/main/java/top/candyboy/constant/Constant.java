package top.candyboy.constant;

import java.io.File;

public class Constant {

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
}
