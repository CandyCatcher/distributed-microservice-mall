package top.candysky.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import top.candysky.pojo.bo.UserBO;
import top.candysky.service.UserService;
import top.candysky.utils.IMOOCJSONResult;

// 这个接口可以添加一些内容
@Api(value = "注册&登录", tags = {"用于注册和登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassPortController {

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
    public IMOOCJSONResult regist(@RequestBody UserBO userBO) {

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
        userService.createUser(userBO);

        return IMOOCJSONResult.ok();
    }


}
