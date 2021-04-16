package top.candyboy.order.service;

import org.apache.dubbo.config.annotation.DubboService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import top.candyboy.enums.OrderStatusEnum;
import top.candyboy.enums.YesOrNo;
import top.candyboy.item.ItemService;
import top.candyboy.facade.order.OrderService;
import top.candyboy.facade.user.AddressService;
import top.candyboy.order.mapper.OrderItemsMapper;
import top.candyboy.order.mapper.OrderStatusMapper;
import top.candyboy.order.mapper.OrdersMapper;
import top.candyboy.pojo.item.Items;
import top.candyboy.pojo.item.ItemsSpec;
import top.candyboy.pojo.order.OrderItems;
import top.candyboy.pojo.order.OrderStatus;
import top.candyboy.pojo.order.Orders;
import top.candyboy.pojo.order.bo.ShopCartBO;
import top.candyboy.pojo.order.bo.SubmitOrderBO;
import top.candyboy.pojo.order.vo.MerchantOrdersVO;
import top.candyboy.pojo.order.vo.OrderVO;
import top.candyboy.pojo.user.UserAddress;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DubboService(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    Sid sid;

    @Autowired
    AddressService addressService;

    @Autowired
    ItemService itemService;

    /*
    订单主表
    user_id             用户id
    receiver_name       收货人快照
    receiver_mobile     收货人手机号码快照
    receiver_address    收获地址快照
    total_amount        订单总价格
    real_pay_amount     实际支付总价格
    post_amount         邮费
    pay_method          支付方式
    left_msg            买家留言
    extand              扩展字段
    is_comment          是否平键
    is_delete           是否删除状态
    created_time        创建时间
    updated_time        更新时间
     */
    @Autowired
    OrdersMapper ordersMapper;

    /*
    订单状态表
    order_id            订单ID
    order_status        订单状态
    created_time        订单创建时间
    pay_time            支付成功时间
    deliver_time        发货时间
    success_time        交易成功时间
    close_time          交易关闭时间
    comment_time        留言时间
     */
    @Autowired
    OrderStatusMapper orderStatusMapper;

    /*
    order_id            订单ID
    item_id             商品ID
    item_img            商品主图片
    item_name           商品名称
    item_spec_id        商品规格ID
    item_spec_name      商品规格名称
    price               商品价格
    buy_counts          购买数量
     */
    @Autowired
    OrderItemsMapper orderItemsMapper;

    @Override
    public OrderVO createOrder(List<ShopCartBO>shopCartList, SubmitOrderBO submitOrderBO) {
        /*
        那么创建一个订单的时候，要创建上面这么几个表
         */

        // 拿到前端传过来的数据
        Integer payMethod = submitOrderBO.getPayMethod();
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        String leftMsg = submitOrderBO.getLeftMsg();
        String userId = submitOrderBO.getUserId();

        Integer postAmount = 0;

        // 1. 新订单主数据保存
        Orders newOrder = new Orders();
        String orderId = sid.nextShort();
        newOrder.setId(orderId);
        newOrder.setUserId(userId);

        UserAddress userAddress = addressService.queryAddressById(userId, addressId);
        newOrder.setReceiverAddress(userAddress.getProvince() + " " + userAddress.getCity() + " " + userAddress.getDistrict() + " " + userAddress.getDetail());
        newOrder.setReceiverMobile(userAddress.getMobile());
        newOrder.setReceiverName(userAddress.getReceiver());

        newOrder.setPostAmount(postAmount);
        newOrder.setPayMethod(payMethod);
        newOrder.setLeftMsg(leftMsg);
        newOrder.setIsComment(YesOrNo.NO.type);
        newOrder.setIsDelete(YesOrNo.NO.type);
        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(new Date());

        // 2. 新订单商品数据保存
        // 需要看前端传过来的数据类型是什么样的

        //Integer buyCounts = 1;

        String[] itemSpecIdArr = itemSpecIds.split(",");
        Integer totalAmount = 0;
        Integer realPayAmount = 0;

        /*
         当商品结算之后，redis的购物车中的数据要清除一下
         这里使用一个list存储即将要结算（清除）的数据
         */
        List<ShopCartBO> toBeRemovedShopcartList = new ArrayList<>();

        for (String itemSpecId : itemSpecIdArr) {

            /*
            整合redis之后，商品的购买数量需要重新从redis的购物车中获取，从cookie中获取会出现数据错误
            这里就是获取redis的购物车
             */
            ShopCartBO cartItem = getCountFromShopCart(shopCartList, itemSpecId);
            Integer buyCounts = cartItem.getBuyCounts();
            toBeRemovedShopcartList.add(cartItem);

            // 1.根据具体的规格id查询商品信息,主要获取价格
            ItemsSpec itemsSpec = itemService.queryItemSpecById(itemSpecId);
            totalAmount += itemsSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemsSpec.getPriceDiscount() * buyCounts;

            // 2.根据商品id，获取商品信息
            String itemId = itemsSpec.getItemId();
            Items item = itemService.queryItemId(itemId);
            String imgUrl = itemService.queryItemMainImagById(itemId);

            // 3.循环保存子订单数据到数据库
            OrderItems subOrderItem = new OrderItems();
            String subOrderId = sid.nextShort();
            subOrderItem.setId(subOrderId);
            subOrderItem.setItemId(itemId);
            subOrderItem.setBuyCounts(buyCounts);
            subOrderItem.setItemImg(imgUrl);
            subOrderItem.setItemName(item.getItemName());
            subOrderItem.setItemSpecId(itemSpecId);
            subOrderItem.setItemSpecName(itemsSpec.getName());
            subOrderItem.setOrderId(orderId);
            subOrderItem.setPrice(itemsSpec.getPriceDiscount());

            orderItemsMapper.insert(subOrderItem);

            // 4.用户提交订单后，规格表中需要扣除库存
            itemService.decreaseItemSpecStock(itemsSpec, buyCounts);

        }

        newOrder.setTotalAmount(totalAmount);
        newOrder.setRealPayAmount(realPayAmount);
        ordersMapper.insert(newOrder);

        // 3. 新订单状态数据保存
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCreatedTime(new Date());
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.value);
        orderStatusMapper.insert(orderStatus);

        // 4. 构建商户订单，用于传给支付中心
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setAmount(realPayAmount + postAmount);
        merchantOrdersVO.setPayMethod(payMethod);

        // 5. 构建自定义订单VO
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrdersVO(merchantOrdersVO);
        // 在这里面设置要删除的商品list，返回到controller中
        orderVO.setToBeRemovedShopcartList(toBeRemovedShopcartList);

        return orderVO;
    }

    private ShopCartBO getCountFromShopCart(List<ShopCartBO> shopCartList, String itemSpecId) {
        for (ShopCartBO sc : shopCartList) {
            if (sc.getSpecId().equals(itemSpecId)) {
                return sc;
            }
        }
        return null;
    }


    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }
}
