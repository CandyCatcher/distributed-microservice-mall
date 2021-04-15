package top.candyboy.pojo.order.vo;

import lombok.Data;
import top.candyboy.pojo.order.bo.ShopCartBO;

import java.util.List;

@Data
public class OrderVO {
    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopCartBO> toBeRemovedShopcartList;
}
