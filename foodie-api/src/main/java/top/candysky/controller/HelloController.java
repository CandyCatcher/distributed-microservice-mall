package top.candysky.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO 两者的区别
//@Controller
@RestController
public class HelloController {

    @GetMapping("/hello")
    public Object Hello() {
        return "hello world";
    }

}
