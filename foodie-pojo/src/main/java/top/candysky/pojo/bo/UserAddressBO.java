package top.candysky.pojo.bo;

import lombok.Data;

@Data
public class UserAddressBO {
    private String addressId;
    private String userId;
    private String receiver;
    private String mobile;
    private String province;
    private String city;
    private String district;
    private String detail;
}
