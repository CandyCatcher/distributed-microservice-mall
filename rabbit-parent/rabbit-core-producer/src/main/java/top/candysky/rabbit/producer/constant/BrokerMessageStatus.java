package top.candysky.rabbit.producer.constant;

public enum BrokerMessageStatus {

    SENDING("0"),
    SEND_OK("1"),
    SEND_FAIL("2"),
    SEND_FALI_MOMENT("3");

    public final String code;

    BrokerMessageStatus(String code) {
        this.code = code;
    }
}
