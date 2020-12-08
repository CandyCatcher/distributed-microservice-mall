package top.candysky.service;

import top.candysky.pojo.bo.SubmitOrderBO;
import top.candysky.pojo.vo.OrderVO;

public interface OrderService {

    public OrderVO createOrder(SubmitOrderBO submitOrderBO);

    public void updateOrderStatus(String orderId, Integer orderStatus);
}
