package top.candysky.kafka.collector.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.candysky.kafka.collector.util.InputMDC;

@Slf4j
@RestController
public class IndexController {

    /*
    [%d{yyyy-MM-dd'T'HH:mm:ss.SSSZZ}]  是US时间格式
    [%level{length=5}] [%thread-%tid]  日志的级别
    [%logger]                          线程的Id
    [%X{hostName}]                     %X表示自定义
    [%X{ip}]
    [%X{applicationName}]
    [%F,%L,%C,%M]                      %F 文件,%L 行数,%C class文件,%M 方法名
    [%m]                               日志输出的内容
    ##                                 自己设置的
    '%ex'%n                            %ex表示抛出异常的信息
                                       %n表示换行
     */
    @RequestMapping(value = "/index")
    public String index() {

        /*
        MDC 可以问为它是绑定了当前线程的ThreadLocal
        调用一下这个方法就能获取到自定义的属性了
         */
        InputMDC.putMDC();

        log.info("我是一条info日志");
        log.warn("我是一条warn日志");
        log.error("我是一条error日志");

        return "index";
    }

}
