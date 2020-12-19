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
        message.setMessageType(MessageType.CONFIRM);
        sendKernal(message);
    }

    /*
    怎么说我的消息百分之百的发送到MQ呢
    业务和消息是强一致性，也就是原子性
    当我们订单创建成功了，要把消息发送给下游的系统进行下一步处理
    要保证我创建订单成功和发送消息成功保持一致，这一个过程confirm是肯定不行的
    因为会出现网络闪断的情况，MQ Broker发送给producer的confirm消息可能没有收到
    所以关于可靠行是需要我们自己进行处理的

    看图，以下完订单为例，BIZ DB中，我们往order表中插入一条记录，sender发送一条信息给Broker。
    MSG DB就是用来存储我们还需要往日志表插入一条记录，也就是我们希望BIZ DB和MSG DB能保持原子性
    如果这两个数据库是同源的，是同一个数据源，那我们加一个事务就能保证他们就强一致了。（同时成功，
    同时失败，失败就会滚）
    第一步成功了，第二步再去发message，Broker收到message，返回confirm，如果confirm成功了，再去更新
    MSG DB里面数据的状态，（第一步刚插入数据的时候，status的值可能为0，也就是已发送待确认），那么此时
    status为1。
    如果MQ Broker发送给producer的confirm时，网络闪断时，status就还是为0，我们可以做一些补偿，
    兜底的策略。比如说当我们把消息发送出去后，以MSG DB落库为准，假设五分钟status还是为0的话，
    我们就能推测Broker没有收到应答，可能出现网络闪断等等状况。这时我们可以通过一个
    分布式定时任务将那些要发送但是Broker没有确认的消息抓取出来，retry send。
    比如我们可以每隔五分钟从MSG DB抓取那些没有发送成功的消息，重新投递
    如果重新投递多次还是没有发送成功，假设设置的retry count=3,那么我就认为这条消息就是失败的，
    那么我就把status设置为2，意思就是消息发送失败，待回滚。怎么回滚呢？我们就是将MSG DB和BIZ DB的数据
    进行回滚，相当于这个订单没有创建过。
    还有一种极端的情况，retry count刚好等于3的时候，Broker收到了消息，consumer已经在处理消息了，
    但是返回confirm时网络闪断，listener没有收到ack，但是count已经大于3了，那就说明该极限情况下，
    我们要把BIZ DB进行回滚了，但是下游consumer已经处理成功了，那么BIZ DB要和下游的数据进行
    一次对比

    还有一个问题
    在高并发的情况下，我们基本上不会使用事务，那怎么保持两个数据库的一致性呢？
    分库分表，负载均衡

     */
    @Override
    public void reliantSend(Message message) {

    }

    @Override
    public void sendMessages() {

    }
}
