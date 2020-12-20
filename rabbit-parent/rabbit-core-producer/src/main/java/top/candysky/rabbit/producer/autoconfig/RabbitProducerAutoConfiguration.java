package top.candysky.rabbit.producer.autoconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import top.candysky.rabbit.task.annotation.EnableElasticJob;

@EnableElasticJob
@Configuration
@ComponentScan({"top.candysky.rabbit.producer.*"})
public class RabbitProducerAutoConfiguration {
}
