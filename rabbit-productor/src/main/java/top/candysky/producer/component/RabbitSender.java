package top.candysky.producer.component;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /*
    这里就是确认消息的回调监听接口，用于确认消息是否被broker收到
     */
    final ConfirmCallback confirmCallback = new ConfirmCallback() {
        /**
         *
         * @param correlationData 作为一个唯一的标识
         * @param b broker是否落盘成功
         * @param s 失败的一些异常信息
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean b, String s) {
            assert correlationData != null;
            System.out.println("消息ACK结果：" + b + ", correlationData:" + correlationData.getId());
        }
    };

    public void send(Object message, Map<String, Object> properties) {
        // 属性状态什么的放在MessageHeaders这里面
        MessageHeaders  messageHeaders = new MessageHeaders(properties);
        // 构造一条消息
        Message<?> msg = MessageBuilder.createMessage(message, messageHeaders);
        // confirm模式需要做一个监听
        rabbitTemplate.setConfirmCallback(confirmCallback);

        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        // 现在把消息发出去
        rabbitTemplate.convertAndSend("exchange-1", "springboot.rabbit", msg,
                // 发送完消息做什么
                new MessagePostProcessor() {
                    @Override
                    public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message) throws AmqpException {

                        System.err.println("--- post to do: " + message);

                        return message;
                    }
                },
                // 作为唯一一个标记
                correlationData
        );

    }

}
