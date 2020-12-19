package top.candysky.rabbit.common.constant;

public enum BrokerMessageStatus {

    SENDING("0"),
    SEND_OK("1"),
    SEND_FALL("2");

    private String code;

    private BrokerMessageStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
