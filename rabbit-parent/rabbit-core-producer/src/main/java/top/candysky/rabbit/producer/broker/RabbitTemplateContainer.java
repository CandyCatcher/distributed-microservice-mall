package top.candysky.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import top.candysky.rabbit.api.Message;
import top.candysky.rabbit.api.MessageType;
import top.candysky.rabbit.api.exception.MessageException;
import top.candysky.rabbit.common.convert.GenericMessageConverter;
import top.candysky.rabbit.common.convert.RabbitMessageConverter;
import top.candysky.rabbit.common.serializer.Serializer;
import top.candysky.rabbit.common.serializer.SerializerFactory;
import top.candysky.rabbit.common.serializer.impl.JacksonSerializerFactory;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback {

    /*
    池化嘛，需要一个容器map来存储
     */
    private Map<String/* TOPIC */, RabbitTemplate> rabbitMap = Maps.newConcurrentMap();

    /*
    然后就需要一个连接工厂，因为用到了RabbitTemplate，那么就需要连接工厂了
     */
    @Autowired
    private ConnectionFactory connectionFactory;

    /*
    饥饿模式
     */
    private SerializerFactory serializerFactory = JacksonSerializerFactory.INSTANCE;

    public static final Splitter splitter = Splitter.on("#");

    /*
    池子就是要向外提供我们需要的 RabbitTemplate，那么这里肯定要有一个get方法
    通过message的topic获取
     */
    public RabbitTemplate getTemplate(Message message) throws MessageException {
        Preconditions.checkNotNull(message);
        String topic = message.getTopic();
        RabbitTemplate rabbitTemplate = rabbitMap.get(topic);
        if (rabbitTemplate != null) {
            return rabbitTemplate;
        }
        log.info("#RabbitTemplateContainer.getTemplate# topic: {} is not exist", message.getTopic());

        /*
        既然没有，那就要新建一个了
         */
        RabbitTemplate newRabbitTemplate = new RabbitTemplate(connectionFactory);
        newRabbitTemplate.setExchange(topic);
        /*
        这是重试的时候需要用到
         */
        newRabbitTemplate.setRetryTemplate(new RetryTemplate());
        newRabbitTemplate.setRoutingKey(message.getRoutingKey());

        /*
        序列化的内容需要用到
        序列化的时候应该做什么事情
        发消息的时候，将自定义的message转换为springframework的message
        接受消息的时候，将springframework的message转换为自定义的message
         */
        Serializer serializer = serializerFactory.create();
        GenericMessageConverter genericMessageConverter = new GenericMessageConverter(serializer);
        RabbitMessageConverter rabbitMessageConverter = new RabbitMessageConverter(genericMessageConverter);
        newRabbitTemplate.setMessageConverter(rabbitMessageConverter);
        //newRabbitTemplate.setMessageConverter();

        // 下面便是处理confirm
        // 只要不是迅速消息，都需要confirmCallback

        String messageType = message.getMessageType();
        if (!messageType.equals(MessageType.RAPID)) {
            newRabbitTemplate.setConfirmCallback(this);
        }

        rabbitMap.putIfAbsent(topic, newRabbitTemplate);

        return  rabbitMap.get(topic);

    }

    /*
    应答的时候应该做什么事情
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // 具体的消息应答
        List<String> splitToList = splitter.splitToList(correlationData.getId());
        String messageId = splitToList.get(0);
        Long sendTime = Long.valueOf(splitToList.get(1));
        if (ack) {

            log.info("send message is OK. confirm messageId: {}, sendTime: {}", messageId, sendTime);
        } else {
            log.info("send message is FAIL. confirm messageId: {}, sendTime: {}", messageId, sendTime);
        }

    }
}
