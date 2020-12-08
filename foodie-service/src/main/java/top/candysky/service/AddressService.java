package top.candysky.service;

import top.candysky.pojo.UserAddress;
import top.candysky.pojo.bo.UserAddressBO;

import java.util.List;

public interface AddressService {

    /**
     * 根据用户Id查询所有的收货地址
     * @param userId
     * @return
     */
    public List<UserAddress> queryAll(String userId);

    /**
     * 添加收货地址
     * @param userAddressBO
     */
    public void addUserAddress(UserAddressBO userAddressBO);

    /**
     * 修改收货地址
     * @param userAddressBO
     */
    public void updateUserAddress(UserAddressBO userAddressBO);

    /**
     * 删除收货地址
     * @param userId
     */
    public void deleteUserAddress(String userId, String addressId);

    /**
     * 删除收货地址
     * @param userId
     */
    public void updateUserAddressToBeDefault(String userId, String addressId);

    /**
     * 根据用户id和地址id查询具体信息
     * @param userId
     * @param addressId
     * @return
     */
    public UserAddress queryAddressById(String userId, String addressId);
}
