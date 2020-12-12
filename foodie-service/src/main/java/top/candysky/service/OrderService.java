package top.candysky.service;

import top.candysky.pojo.bo.ShopCartBO;
import top.candysky.pojo.bo.SubmitOrderBO;
import top.candysky.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {

    public OrderVO createOrder(List<ShopCartBO> shopCartList, SubmitOrderBO submitOrderBO);

    public void updateOrderStatus(String orderId, Integer orderStatus);
}
