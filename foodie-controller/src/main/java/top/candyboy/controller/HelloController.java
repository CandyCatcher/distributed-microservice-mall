package top.candyboy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// 使用这个注解，这个controller就不会在swagger文档中出现了
@ApiIgnore
// TODO 两者的区别
//@Controller
@RestController
public class HelloController {

    final static Logger logger = LoggerFactory.getLogger("HelloController");

    @GetMapping("/hello")
    public Object Hello() {

        logger.debug("info: hello");
        logger.info("info: hello");
        logger.warn("info: hello");
        logger.error("info: hello");

        return "hello world";
    }

    /**
     * 这样使用springSession
     */
    public Object setSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userInfo", "new user");
        session.setMaxInactiveInterval(3600);
        session.getAttribute("userIngo");
        return "ok";
    }

}
