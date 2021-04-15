package top.candyboy.enums;

public enum OrderStatusEnum {

    WAIT_PAY("待付款", 10),
    WAIT_DELIVER("已付款，待发货", 20),
    WAIT_RECEIVE("已发货，待收货", 30),
    SUCCESS("交易成功", 40),
    CLOSE("交易关闭", 50);

    public String type;
    public Integer value;

    OrderStatusEnum(String type, Integer value) {
        this.type = type;
        this.value = value;
    }
}
