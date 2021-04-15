package top.candyboy.order.pojo.vo;

import lombok.Data;
import top.candyboy.order.pojo.bo.ShopCartBO;

import java.util.List;

@Data
public class OrderVO {
    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopCartBO> toBeRemovedShopcartList;
}
