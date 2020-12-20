package top.candysky.rabbit.producer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.candysky.rabbit.producer.constant.BrokerMessageStatus;
import top.candysky.rabbit.producer.entity.BrokerMessage;
import top.candysky.rabbit.producer.mapper.BrokerMessageMapper;

import java.util.Date;
import java.util.List;

@Service
public class MessageStroreService {

    @Autowired
    private BrokerMessageMapper brokerMessageMapper;

    public BrokerMessage query(String messageId) {
        return brokerMessageMapper.selectByPrimaryKey(messageId);
    }

    public int insert(BrokerMessage brokerMessage) {
        return brokerMessageMapper.insert(brokerMessage);
    }

    public void success(String messageId) {
        brokerMessageMapper.changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_OK.code,new Date());
    }

    public void failure(String messageId) {
        brokerMessageMapper.changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_FAIL.code, new Date());
    }

    public List<BrokerMessage> fetcheTimeOutMessage4Retry(BrokerMessageStatus brokerMessageStatus) {
        return brokerMessageMapper.queryBrokerMessageStatus(brokerMessageStatus.code);
    }

    public void updateTryCount(String messageId) {
        brokerMessageMapper.update4TryCount(messageId, new Date());
    }
}
