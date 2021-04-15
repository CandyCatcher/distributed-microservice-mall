package top.candyboy.order.mapper;

import org.apache.ibatis.annotations.Param;
import top.candyboy.pojo.order.OrderStatus;
import top.candyboy.pojo.order.vo.MyOrdersVO;

import java.util.List;
import java.util.Map;

public interface OrdersMapperCustom {

    public List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String, Object> map);

    public int getMyOrderStatusCounts(@Param("paramsMap") Map<String, Object> map);

    public List<OrderStatus> getMyOrderTrend(@Param("paramsMap") Map<String, Object> map);

}
