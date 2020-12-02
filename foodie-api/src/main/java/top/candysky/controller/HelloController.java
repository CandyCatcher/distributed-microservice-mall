package top.candysky.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

// 使用这个注解，这个controller就不会在swagger文档中出现了
@ApiIgnore
// TODO 两者的区别
//@Controller
@RestController
public class HelloController {

    @GetMapping("/hello")
    public Object Hello() {
        return "hello world";
    }

}
