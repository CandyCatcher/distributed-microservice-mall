package top.candyboy.pojo.center.bo;

import lombok.Data;

@Data
public class OrderItemsCommentBO {

    private String commentId;
    private String itemId;
    private String itemName;
    private String itemSpecId;
    private String itemSpecName;
    private Integer commentLevel;
    private String content;
}