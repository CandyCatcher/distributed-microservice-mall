package top.candysky.rabbit.producer.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.candysky.rabbit.api.Message;
import top.candysky.rabbit.api.MessageType;

@Component
@Slf4j
public class RabbitBrokerImpl implements RabbitBroker{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void rapidSend(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendKernal(message);
    }

    /**
     * 发送消息的核心方法,使用异步线程池
     */
    private void sendKernal(Message message) {

        AsyncBaseQueue.submit((Runnable) () -> {
            CorrelationData correlationData = new CorrelationData(String.format("%s#%s",
                    message.getMessageId(), System.currentTimeMillis()));
            String topic = message.getTopic();
            String routingKey = message.getRoutingKey();
            rabbitTemplate.convertAndSend(topic,routingKey, message, correlationData);

            log.info("#RabbitBrokerImpl.sendKernal# send to rabbitmq, messageId:{}", message.getMessageId());
        });

        /*
        调用这个方法进行异步传输消息，但是参数的设置什么的，影响性能
         */
    }

    @Override
    public void confirmSend(Message message) {

    }

    @Override
    public void reliantSend(Message message) {

    }

    @Override
    public void sendMessages() {

    }
}
