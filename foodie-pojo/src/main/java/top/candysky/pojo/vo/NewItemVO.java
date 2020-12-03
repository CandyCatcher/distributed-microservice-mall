package top.candysky.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class NewItemVO {
    private Integer rootCatId;
    private String rootCatName;
    private String slogan;
    private String catImage;
    private String bgColor;

    private List<SimpleItemVO> simpleItemVOList;
}
