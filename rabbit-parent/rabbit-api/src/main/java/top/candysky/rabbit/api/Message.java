package top.candysky.rabbit.api;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Message implements Serializable {

    private static final long serialVersionUID = -8916788519298037863L;

    /*
    消息的唯一ID
    */
    private String messageId;

    /*
    实际上是一个exchange类型，但是我们可以把它当做topic类型
    还有其它一些类型

    消息的主题
    */
    private String topic;

    /*
    消息的路由规则
     */
    private String routingKey = "";

    /*
    消息的属性
     */
    private Map<String, Object> attributes = new HashMap<>();

    /*
    延迟消息的参数配置
     */
    private int delayMills;

    /*
    消息的类型
    默认的类型是
     */
    private String messageType = MessageType.CONFIRM;

    public Message(String messageId, String topic, String routingKey, Map<String, Object> attributes, int delayMills, String messageType) {
        this.messageId = messageId;
        this.topic = topic;
        this.routingKey = routingKey;
        this.attributes = attributes;
        this.delayMills = delayMills;
        this.messageType = messageType;
    }

    public Message() {
    }
}
