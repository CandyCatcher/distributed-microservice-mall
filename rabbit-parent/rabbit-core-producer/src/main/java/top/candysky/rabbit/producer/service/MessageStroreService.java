package top.candysky.rabbit.producer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.candysky.rabbit.producer.entity.BrokerMessage;
import top.candysky.rabbit.producer.mapper.BrokerMessageMapper;

@Service
public class MessageStroreService {

    @Autowired
    private BrokerMessageMapper brokerMessageMapper;

    public int insert(BrokerMessage brokerMessage) {
        return brokerMessageMapper.insert(brokerMessage);
    }

}
