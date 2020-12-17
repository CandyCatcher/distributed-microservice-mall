package top.candysky.producer.component;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitReceiver {

    @RabbitListener(
            // 可以将这些配置卸载properties文件中
            bindings = @QueueBinding(
                    value = @Queue(value = "queue-1", durable = "true"),
                    // durable是否持久化
                    // 比如这样exchange = @Exchange(name = "${spring.rabbitmq.listener.order.exchange.name}")
                    exchange = @Exchange(name = "exchange-1", durable = "true", type = "topic"),
                    // 忽略异常
                    ignoreDeclarationExceptions = "true",
                    key = "springboot.*"
            )

    )
    //这个注解说明是要监听了
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws IOException {
        // 1.收到消息以后进行业务端处理
        System.out.println("-------------------");
        System.out.println("消费消息" + message.getPayload());

        // 2.处理成功后获取deliveryTag，并进行手工的ACK操作，因为配置文件里配置的是手工签收
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //    消费完了要进行确认
        channel.basicAck(deliveryTag, false);
    }
}
