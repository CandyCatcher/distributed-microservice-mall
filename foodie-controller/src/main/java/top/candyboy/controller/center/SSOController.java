package top.candyboy.controller.center;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import top.candyboy.user.pojo.Users;
import top.candyboy.user.pojo.vo.UsersVO;
import top.candyboy.utils.IMOOCJSONResult;
import top.candyboy.utils.JsonUtils;
import top.candyboy.utils.MD5Utils;
import top.candyboy.redis.RedisOperator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static top.candyboy.controller.BaseController.REDIS_USER_TOKEN;

// 使用这个注解，这个controller就不会在swagger文档中出现了
@ApiIgnore
// TODO 两者的区别
//@Controller
@Controller
public class SSOController {

    final static Logger logger = LoggerFactory.getLogger("HelloController");

    public static final String REDIS_USER_TICKET = "redis_user_ticket";
    public static final String REDIS_TMP_TICKET = "redis_tmp_ticket";
    private static final String COOKIE_USER_TICKET = "cookie_user_ticket";


    @Autowired
    UserService userService;

    @Autowired
    public RedisOperator redisOperator;

    // 当做页面了
    @GetMapping("/login")
    @ResponseBody
    public String login(String returnUrl, Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("returnUrl", returnUrl);

        //获取userTIcket全局门票，如果cookie中能够获取到，证明用户登录过，此时签发一个临时票据
        String userTicket = getCookie(request, COOKIE_USER_TICKET);

        boolean verifyUserTicket = verifyUserTicket(userTicket);

        if (verifyUserTicket) {
            String tmpTicket = createTmpTicket();
            return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
        }

        // 用户从未登陆过，第一次进入则跳转到CAS的统一登录页面
        return "login";
    }

    @GetMapping("/logout")
    @ResponseBody
    public IMOOCJSONResult doLogout( String userId, HttpServletRequest request, HttpServletResponse response) {
        // 1.获取CAS中的用户门票
        String userTicket = getCookie(request, COOKIE_USER_TICKET);

        // 2.清楚userTicket的票据，redis/cookie
        deleteCookie(COOKIE_USER_TICKET, response);
        redisOperator.del(REDIS_USER_TICKET + ":" + userTicket);

        // 3.清楚用户全局会话
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        return IMOOCJSONResult.ok();
    }

    private void deleteCookie(String key, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, null);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    /**
     * 校验CAS全局用户门票
     */
    private boolean verifyUserTicket(String userTicket) {
        // 验证CAS门票不能为空
        if (StringUtils.isBlank(userTicket)) {
            return false;
        }

        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        // 因为是redis，所以要检验
        if (StringUtils.isBlank(userId)) {
            return false;
        }

        // 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return false;
        }

        return true;
    }

    /**
     * cas的统一登录接口
     *  目的：
     *  1. 登录后创建用户的全局会话    >  uniqueToken
     *  2. 创建用户全局门票，用以表示在CAS端是否登录  ->  userTicket
     *  3. 创建用户的临时票据，用于回跳回传   ->  tmpTicket
     */
    public String doLogin( String username, String password, String returnUrl, Model model, HttpServletRequest request,
                           HttpServletResponse response) {
        // 1.判断用户名和密码都不能为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            model.addAttribute("errmsg", "用户名和密码不能为空");
            return "login";

        }

        // 2. 实现登录
        Users userResult = null;
        try {
            userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userResult == null) {
            model.addAttribute("errmsg", "用户名或密码不错误");
            return "login";
        }

        // 实现用户的redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(), JsonUtils.objectToJson(usersVO));

        // 3.生成ticket门票，全局门票，代表用户在 CAS端登陆了
        String userTicket = UUID.randomUUID().toString().trim();

        // 3.1将用户全局门票放在cas端的cookie中
        setCookie(COOKIE_USER_TICKET, userTicket, response);

        // 4.userTicket关联用户id，并且放入到redis中，代表这个用户有门票了，可以去各个景点了
        redisOperator.set(REDIS_USER_TICKET + ":" + userTicket, usersVO.getId());

        // 5.生成临时票据，回跳到调用端网站，是由CAS端所签发的一个一次性的临时ticket
        String tmpTicket = createTmpTicket();

        /*
        userTicket: 用于表示用户在CAS端的一个登陆状态：已登录
        tmpTicket：用于颁发给用户进行一次性的验证的票据，有时效性
         */


        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
    }

    // 校验临时票据
    public IMOOCJSONResult verifyTmpTicket(String tmpTicket,  Model model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        /*
        使用一次性临时票据来验证用户是否登录，如果登陆过，把用户会话信息返回给站点
        使用完毕后，需要销毁临时票据
         */
        String tmpTicketValue = redisOperator.get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketValue)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }

        // 如果临时票据OK，那么需要销毁，并且拿到CAS端cookie中的全局userTicket，一次再获取用户会话
        if (!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        } else {
            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);
        }

        // 验证并获取用户的userTicket
        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        // 因为是redis，所以要检验
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }

        // 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }

        // 验证成功，返回OK，写的爱用户会话
        return IMOOCJSONResult.ok(JsonUtils.jsonToPojo(userRedis, UsersVO.class));
    }

    //创建临时票据
    private String createTmpTicket() {
        String tmpTicket = UUID.randomUUID().toString().trim();
        try {
            redisOperator.set(REDIS_TMP_TICKET + ":" + tmpTicket, MD5Utils.getMD5Str(tmpTicket), 600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpTicket;
    }

    private void setCookie(String key, String val, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, val);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || StringUtils.isBlank(key)) {
            return null;
        }

        String cookieValue = null;
        for (Cookie cookie : cookies) {
            if (cookie.equals(key)) {
                cookieValue = cookie.getValue();
                break;
            }
        }
        return cookieValue;
    }

}
