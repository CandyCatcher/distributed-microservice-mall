package top.candysky.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.candysky.rabbit.api.Message;
import top.candysky.rabbit.api.MessageProducer;
import top.candysky.rabbit.api.MessageType;
import top.candysky.rabbit.api.SendCallBack;
import top.candysky.rabbit.api.exception.MessageRunTimeException;

import java.util.List;

@Component
public class ProducerClient implements MessageProducer {

    @Autowired
    private RabbitBroker rabbitBroker;

    @Override
    public void send(Message message, SendCallBack callBack) throws MessageRunTimeException {
        Preconditions.checkNotNull(message.getTopic());
        String messageType = message.getMessageType();
        switch (messageType) {
            case MessageType.RAPID:
                rabbitBroker.rapidSend(message);
                break;
            case MessageType.CONFIRM:
                rabbitBroker.confirmSend(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.reliantSend(message);
                break;
            default:
                break;
        }
    }

    @Override
    public void send(Message message) throws MessageRunTimeException {

    }

    /*
    发送批量消息
    */
    @Override
    public void send(List<Message> messageList) throws MessageRunTimeException {
        messageList.forEach(message -> {
            message.setMessageType(MessageType.RAPID);
            MessageHolder.add(message);
        });
        rabbitBroker.sendMessages();
    }
}
