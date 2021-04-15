package top.candyboy.pojo.order.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用户中心，我的订单列表VO
 */
@Data
public class MyOrdersVO {

    private String orderId;
    private Date createdTime;
    private Integer payMethod;
    private Integer realPayAmount;
    private Integer postAmount;
    private Integer isComment;
    private Integer orderStatus;

}