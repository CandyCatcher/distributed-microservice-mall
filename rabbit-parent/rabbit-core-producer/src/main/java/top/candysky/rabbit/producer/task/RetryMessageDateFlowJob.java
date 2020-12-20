package top.candysky.rabbit.producer.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.candysky.rabbit.producer.broker.RabbitBroker;
import top.candysky.rabbit.producer.constant.BrokerMessageStatus;
import top.candysky.rabbit.producer.entity.BrokerMessage;
import top.candysky.rabbit.producer.service.MessageStroreService;
import top.candysky.rabbit.task.annotation.ElasticJobConfig;

import java.util.List;

import static top.candysky.rabbit.producer.constant.BrokerMessageConst.MAX_RETRY_COUNT;

@Component
@ElasticJobConfig(
        name = "top.candysky.rabbit.producer.task.RetryMessageDateFlowJob ",
        cron = "0/10 * * * * ?", /* 任何时间 */
        description = "可靠性投递消息补偿任务",
        overwrite = true,
        shardingTotalCount = 1 /* 针对一个数据库的表的数量 */
)
public class RetryMessageDateFlowJob implements DataflowJob<BrokerMessage> {

    /*
    重新发送消息要操作数据库
     */
    @Autowired
    private MessageStroreService messageStroreService;

    @Autowired
    private RabbitBroker rabbitBroker;

    @Override
    public List<BrokerMessage> fetchData(ShardingContext shardingContext) {
        return messageStroreService.fetcheTimeOutMessage4Retry(BrokerMessageStatus.SEND_FAIL);
    }

    @Override
    public void processData(ShardingContext shardingContext, List<BrokerMessage> list) {
        list.forEach(brokerMessage -> {
            if (brokerMessage.getTryCount() >= MAX_RETRY_COUNT) {
                messageStroreService.failure(brokerMessage.getMessageId());
            } else {
                // 需要重新计数
                messageStroreService.updateTryCount(brokerMessage.getMessageId());
                // 重新发送消息
                rabbitBroker.reliantSend(brokerMessage.getMessage());
            }
        });
    }
}
