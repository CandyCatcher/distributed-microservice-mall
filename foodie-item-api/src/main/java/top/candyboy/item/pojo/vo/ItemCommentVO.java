package top.candyboy.item.pojo.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ItemCommentVO {
    private Integer commentLevel;
    private String content;
    private Date createdTime;
    private String specName;
    private String nickName;
    private String userFace;
}
