package top.candysky.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.candysky.service.UserService;
import top.candysky.utils.IMOOCJSONResult;

@RestController
@RequestMapping("passport")
public class PassPortController {

    @Autowired
    UserService userService;

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


}
