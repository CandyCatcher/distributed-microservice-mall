package top.candyboy.pojo.order.vo;

import lombok.Data;

@Data
public class MerchantOrdersVO {
    private String merchantOrderId;
    private String merchantUserId;
    private Integer amount;
    private Integer payMethod;
    private String returnUrl;
}
