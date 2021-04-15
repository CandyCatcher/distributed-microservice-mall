package top.candyboy.center.mapper;

import top.candyboy.user.pojo.Users;

public interface UsersMapper {
    public Users selectByPrimaryKey(String userId);

    public void updateByPrimaryKeySelective(Users updateUser);
}
