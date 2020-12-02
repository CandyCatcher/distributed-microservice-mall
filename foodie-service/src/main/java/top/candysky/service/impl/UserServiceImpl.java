package top.candysky.service.impl;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.candysky.enums.Sex;
import top.candysky.mapper.UsersMapper;
import top.candysky.pojo.Users;
import top.candysky.pojo.bo.UserBO;
import top.candysky.service.UserService;
import top.candysky.utils.DateUtil;
import top.candysky.utils.MD5Utils;

import java.util.Date;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    public static final String USER_FACE = "http://images.candysky.top/config_file/%E5%A4%B4%E5%83%8F.jpeg";

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Example userExample = new Example(Users.class);

        //创建条件
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username", username);

        //查询
        Users users = usersMapper.selectOneByExample(userExample);

        return users != null;
    }

    // 传到后端是个json，其实就是一个对象
    @Override
    public Users createUser(UserBO userBO) {

        String userId = sid.nextShort();

        Users user = new Users();

        user.setId(userId);
        try {
            user.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setUsername(userBO.getUsername());
        // 默认用户昵称等同于用户名
        user.setNickname(userBO.getUsername());
        // 默认的头像
        user.setFace(USER_FACE);
        // 默认的出生日期
        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        // 默认的性别为保密
        user.setSex(Sex.secret.type);
        // 创建日期
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);

        return user;
    }
}
