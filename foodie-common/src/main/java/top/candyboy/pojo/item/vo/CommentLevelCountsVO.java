package top.candyboy.pojo.item.vo;

import lombok.Data;

@Data
public class CommentLevelCountsVO {
    private Integer totalCounts;
    private Integer goodCounts;
    private Integer normalCounts;
    private Integer badCounts;
}
