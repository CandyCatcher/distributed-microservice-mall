package top.candysky.rabbit.api;

import top.candysky.rabbit.api.exception.MessageRunTimeException;

import java.util.List;

public interface MessageProducer {

    /**
     * 发送单条消息
     * 附带sendCallBack回调，执行相应的业务逻辑
     */
    void send(Message message, SendCallBack callBack) throws MessageRunTimeException;

    /**
     * message消息的发送
     */
    void send(Message message) throws MessageRunTimeException;

    /**
     * 消息的批量发送
     */
    void send(List<Message> messageList) throws MessageRunTimeException;

}
