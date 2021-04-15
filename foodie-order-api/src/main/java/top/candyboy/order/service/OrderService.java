package top.candyboy.order.service;

import top.candyboy.order.pojo.bo.ShopCartBO;
import top.candyboy.order.pojo.bo.SubmitOrderBO;
import top.candyboy.order.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {

    public OrderVO createOrder(List<ShopCartBO> shopCartList, SubmitOrderBO submitOrderBO);

    public void updateOrderStatus(String orderId, Integer orderStatus);
}
