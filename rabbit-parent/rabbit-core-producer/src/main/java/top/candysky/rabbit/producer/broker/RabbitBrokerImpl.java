package top.candysky.rabbit.producer.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.candysky.rabbit.api.Message;
import top.candysky.rabbit.api.MessageType;
import top.candysky.rabbit.api.exception.MessageException;

/**
 * 在这里做一个池化的操作
 * '@Autowired'引入的rabbitTemplate是单例的
 * 1. 每一个topic对应一个RabbitTemplate，提高发送效率
 * 2. 根据不同的需求指定化不同的模版
 * 之后再增加其他的类型，直接在continer中添加就好了
 *
 * 每一个topic对应的routingKey都是不一样的
 * 假设用的是rabbitTemplate，假设routingKey是rabbit.*,
 * 假设后面改成了spring.*，那么每次都要进行手动重新的设置，很麻烦
 */
@Component
@Slf4j
public class RabbitBrokerImpl implements RabbitBroker{

    //@Autowired
    //private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitTemplateContainer rabbitTemplateContainer;

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
            RabbitTemplate template = null;
            try {
                template = rabbitTemplateContainer.getTemplate(message);
            } catch (MessageException e) {
                e.printStackTrace();
            }
            assert template != null;
            template.convertAndSend(topic,routingKey, message, correlationData);

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
