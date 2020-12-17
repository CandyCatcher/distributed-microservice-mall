package top.candysky.rabbit.producer.broker;

import org.springframework.stereotype.Component;
import top.candysky.rabbit.api.Message;

/**
 * 具体发送不同类型的消息
 */
@Component
public interface RabbitBroker {

    void rapidSend(Message message);

    void confirmSend(Message message);

    void reliantSend(Message message);

    void sendMessages();
}
