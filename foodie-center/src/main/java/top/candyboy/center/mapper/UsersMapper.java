package top.candyboy.center.mapper;

import top.candyboy.pojo.user.Users;

public interface UsersMapper {
    public Users selectByPrimaryKey(String userId);

    public void updateByPrimaryKeySelective(Users updateUser);
}
