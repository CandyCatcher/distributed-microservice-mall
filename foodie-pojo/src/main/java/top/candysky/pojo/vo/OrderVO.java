package top.candysky.pojo.vo;

import lombok.Data;
import top.candysky.pojo.bo.ShopCartBO;

import java.util.List;

@Data
public class OrderVO {
    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopCartBO> toBeRemovedShopcartList;
}
