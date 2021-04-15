package top.candyboy.item.pojo.vo;

import lombok.Data;

/**
 * 三级分类
 */
@Data
public class SubCategoryVO {
    // 要和查询设置的别名一样
    private Integer subId;
    private String subName;
    private Integer subType;
    private Integer subFatherId;
}
