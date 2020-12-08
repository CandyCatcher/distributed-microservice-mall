package top.candysky.enums;

public enum PayMethod {

    WECHAT("微信支付", 1),
    ALIPAY("支付宝", 2);

    public final Integer value;
    public final String type;

    PayMethod(String type, Integer value) {
        this.value = value;
        this.type = type;
    }
}
