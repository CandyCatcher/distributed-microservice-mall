package top.candysky.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import top.candysky.pojo.Users;
import top.candysky.pojo.bo.UserBO;
import top.candysky.service.UserService;
import top.candysky.utils.CookieUtils;
import top.candysky.utils.IMOOCJSONResult;
import top.candysky.utils.JsonUtils;
import top.candysky.utils.MD5Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 这个接口可以添加一些内容
@Api(value = "注册&登录", tags = {"用于注册和登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassPortController {

    final static Logger logger = LoggerFactory.getLogger("PassPortController");

    @Autowired
    UserService userService;

    // 用于阐述当前方法的内容
    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    // @RequestParam说明是请求的参数不是什么url值
    public IMOOCJSONResult usernameIsExist(@RequestParam String username) {

        // 判空
        if (StringUtils.isBlank(username)) {
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }

        // 判断是否存在
        if (userService.queryUsernameIsExist(username)) {
            return IMOOCJSONResult.errorMap("用户名已存在");
        }

        return IMOOCJSONResult.ok();
    }

    @PostMapping("/regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        // 1.判断用户名和密码都不能为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)) {
            return IMOOCJSONResult.errorMsg("用户名和密码都不能为空");
        }

        // 2.查询用户名是否存在
        if (userService.queryUsernameIsExist(username)) {
            return IMOOCJSONResult.errorMsg("用户名已存在");
        }

        // 3.判断两个密码是否一样
        if (!password.equals(confirmPassword)) {
            return IMOOCJSONResult.errorMsg("输入的两次密码不一致");
        }

        // 4.密码长度不能少于6位
        if (password.length() < 6) {
            return IMOOCJSONResult.errorMsg("密码长度小于6位");
        }

        // 5. 实现注册
        Users userResult = userService.createUser(userBO);
        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户名登录", httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        // 1.判断用户名和密码都不能为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return IMOOCJSONResult.errorMsg("用户名和密码都不能为空");
        }
        // 2. 实现登录
        Users userResult = null;
        try {
            userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userResult == null) {
            return IMOOCJSONResult.errorMsg("用户名或密码错误");
        }

        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);

        return IMOOCJSONResult.ok(userResult);
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId, HttpServletRequest request, HttpServletResponse response) {

        /*
        以前的话，如果用户要退出登录，是要清除session中的登录信息，在这里就不需要做，我们只是使用了一个cookie
         */
        CookieUtils.deleteCookie(request, response, "user");

        // TODO 用户退出登录需要清空购物车
        // TODO 分布式会话需要清除用户数据

        return IMOOCJSONResult.ok();
    }

    private Users setNullProperty(Users userResult) {

        userResult.setPassword(null);
        userResult.setRealname(null);
        userResult.setMobile(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setBirthday(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);

        return userResult;
    }

}
