package top.candysky.service.impl;

import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.candysky.enums.YesOrNo;
import top.candysky.mapper.UserAddressMapper;
import top.candysky.pojo.UserAddress;
import top.candysky.pojo.bo.UserAddressBO;
import top.candysky.service.AddressService;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserAddressMapper userAddressMapper;

    @Autowired
    Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAll(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        // 用这个方法就能查询出所有该id的信息
        return userAddressMapper.select(userAddress);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void addUserAddress(UserAddressBO userAddressBO) {
        // 这个地方不是检查数据全不全，检查数据全不全是在controller中检查
        // 检查的是用户是否存在地址，如果没有，则新增的就是'默认地址'
        Integer isDefault = 0;
        List<UserAddress> userAddressList = queryAll(userAddressBO.getUserId());
        if (userAddressList.size() == 0) {
            isDefault = 1;
        }

        // 保存到数据库
        // 还需要对页面传过来的数据包装一下，添加'创建日期'、'修改日期'、'地址ID-addressId'
        String addressId = sid.nextShort();
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(userAddressBO, userAddress);

        userAddress.setId(addressId);
        userAddress.setIsDefault(isDefault);
        userAddress.setCreatedTime(new Date());
        userAddress.setUpdatedTime(new Date());

        userAddressMapper.insert(userAddress);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void updateUserAddress(UserAddressBO userAddressBO) {
        String addressId = userAddressBO.getAddressId();
        UserAddress pendingAddress = new UserAddress();
        BeanUtils.copyProperties(userAddressBO, pendingAddress);
        pendingAddress.setId(addressId);
        pendingAddress.setUpdatedTime(new Date());
        userAddressMapper.updateByPrimaryKeySelective(pendingAddress);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void deleteUserAddress(String userId, String id) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(id);
        userAddress.setUserId(userId);
        userAddressMapper.delete(userAddress);
    }

    @Override
    public void updateUserAddressToBeDefault(String userId, String addressId) {
        // 1.查找默认地址，原来默认的设置为非默认
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setIsDefault(YesOrNo.YES.type);
        List<UserAddress> list = userAddressMapper.select(userAddress);
        for (UserAddress tempUserAddress : list) {
            tempUserAddress.setIsDefault(YesOrNo.NO.type);
            userAddressMapper.updateByPrimaryKeySelective(tempUserAddress);
        }
        // 2.将点击的设置为默认地址
        UserAddress defaultAddress = new UserAddress();
        defaultAddress.setUserId(userId);
        defaultAddress.setId(addressId);
        defaultAddress.setIsDefault(YesOrNo.YES.type);
        userAddressMapper.updateByPrimaryKeySelective(defaultAddress);
    }
}
