package top.candyboy.user.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

// 右侧的说明
@ApiModel(value = "用户对象BO", description = "从客户端、用户传来的数据封装在此")
@Data
public class UserBO {

    @ApiModelProperty(value = "用户名", name = "username", example = "candy boy", required = true)
    private String username;
    @ApiModelProperty(value = "密码", name = "password", example = "1234", required = true)
    private String password;
    @ApiModelProperty(value = "确认密码", name = "confirmPassword", example = "1234", required = false)
    private String confirmPassword;
}
