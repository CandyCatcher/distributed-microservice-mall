package top.candyboy.facade.order;

import top.candyboy.pojo.order.bo.ShopCartBO;
import top.candyboy.pojo.order.bo.SubmitOrderBO;
import top.candyboy.pojo.order.vo.OrderVO;

import java.util.List;

public interface OrderService {

    public OrderVO createOrder(List<ShopCartBO> shopCartList, SubmitOrderBO submitOrderBO);

    public void updateOrderStatus(String orderId, Integer orderStatus);
}
