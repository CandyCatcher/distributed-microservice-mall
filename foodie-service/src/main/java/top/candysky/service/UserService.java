package top.candysky.service;

import top.candysky.pojo.Users;
import top.candysky.pojo.bo.UserBO;

public interface UserService {
    /**
     * 判断用户名是否存在
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 用户注册
     * @param userBO
     * @return
     */
    public Users createUser(UserBO userBO);

    /**
     * 检索用户登录的用户名和密码是否匹配
     * @param username
     * @param password
     * @return
     */
    public Users queryUserForLogin(String username, String password);
}
