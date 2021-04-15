package top.candyboy.item.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 这里面放的是二级分类
 */
@Data
public class CategoryVO {
    private Integer id;
    private String name;
    private Integer type;
    private Integer fatherId;
    // 二级分类里面还有三级分类
    private List<SubCategoryVO> subCatList;
}
