package top.candyboy.controller.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import top.candyboy.utils.RedisOperator;

@RestController
@RequestMapping("redis")
public class RedisController {

    final static Logger logger = LoggerFactory.getLogger("RedisController");


}
