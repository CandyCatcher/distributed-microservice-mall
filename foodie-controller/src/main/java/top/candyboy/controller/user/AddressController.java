package top.candyboy.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.candyboy.user.pojo.UserAddress;
import top.candyboy.user.pojo.bo.UserAddressBO;
import top.candyboy.user.service.AddressService;
import top.candyboy.utils.IMOOCJSONResult;
import top.candyboy.utils.MobileEmailUtils;

import java.util.List;

/**
 * 用户在确认订单页面，可以针对收货地址做如下操作
 * 1.查询用户所有的收货地址列表
 * 2.新增收货地址
 * 3.删除收货地址
 * 4.修改收货地址
 * 5.设置默认地址
 */
@Api(value = "用户收货地址", tags = "收货地址相关的api")
@RestController
@RequestMapping("address")
public class AddressController {

    final static Logger logger = LoggerFactory.getLogger("AddressController");

    @Autowired
    private AddressService addressService;

    @ApiOperation(value = "根据用户id查询用户所有的收货地址列表", notes = "查询用户所有的收货地址列表", httpMethod = "GET")
    @RequestMapping("/list")
    public IMOOCJSONResult list(@RequestParam String userId) {

        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        List<UserAddress> addressList = addressService.queryAll(userId);

        return IMOOCJSONResult.ok(addressList);
    }

    @ApiOperation(value = "用户新增收货地址", notes = "查询用户所有的收货地址列表", httpMethod = "POST")
    @RequestMapping("/add")
    public IMOOCJSONResult add(@RequestBody UserAddressBO userAddressBO) {

        // 检查数据是否齐全
        IMOOCJSONResult checkResult = checkAddress(userAddressBO);
        // 在这里看方法返回是否正确
        if (checkResult.getStatus() != 200) {
            return checkResult;
        }

        addressService.addUserAddress(userAddressBO);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户修改收货地址", notes = "修改用户的收货地址", httpMethod = "POST")
    @RequestMapping("/update")
    public IMOOCJSONResult update(@RequestBody UserAddressBO userAddressBO) {

        // 检查数据是否齐全
        IMOOCJSONResult checkResult = checkAddress(userAddressBO);
        // 在这里看方法返回是否正确
        if (checkResult.getStatus() != 200) {
            return checkResult;
        }

        addressService.updateUserAddress(userAddressBO);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户删除收货地址", notes = "删除用户的收货地址", httpMethod = "POST")
    @RequestMapping("/delete")
    public IMOOCJSONResult delete(@RequestParam String userId,
                                  @RequestParam String addressId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return IMOOCJSONResult.errorMsg("删除错误");
        }

        addressService.deleteUserAddress(userId, addressId);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "重新设置默认收货地址", notes = "设置默认收货地址", httpMethod = "POST")
    @RequestMapping("/setDefault")
    public IMOOCJSONResult setDefault(@RequestParam String userId,
                                  @RequestParam String addressId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return IMOOCJSONResult.errorMsg("设置出现错误");
        }

        addressService.updateUserAddressToBeDefault(userId, addressId);

        return IMOOCJSONResult.ok();
    }

    private IMOOCJSONResult checkAddress(UserAddressBO userAddressBO) {

        // 收货地址、收货人不能为空
        String receiver = userAddressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return IMOOCJSONResult.errorMap("收货人不能为空");
        }

        // 手机号不能为空
        String mobile = userAddressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return IMOOCJSONResult.errorMap("手机号码不能为空");
        }
        if (mobile.length() < 11) {
            return IMOOCJSONResult.errorMsg("手机号码长度不正确");
        }
        boolean checkMobileIsOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!checkMobileIsOk) {
            return IMOOCJSONResult.errorMsg("手机号码格式不正确");
        }

        String province = userAddressBO.getProvince();
        String city = userAddressBO.getCity();
        String district = userAddressBO.getDistrict();
        if (StringUtils.isBlank(province) || StringUtils.isBlank(city) || StringUtils.isBlank(district)) {
            return IMOOCJSONResult.errorMap("收货地址信息不齐全");
        }
        return IMOOCJSONResult.ok();
    }

}
