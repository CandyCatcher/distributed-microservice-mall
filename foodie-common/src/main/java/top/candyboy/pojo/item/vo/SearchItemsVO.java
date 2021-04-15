package top.candyboy.pojo.item.vo;

import lombok.Data;

@Data
public class SearchItemsVO {
    private String itemId;
    private String itemName;
    private Integer sellCounts;
    private String imgUrl;
    private Integer price;
}
