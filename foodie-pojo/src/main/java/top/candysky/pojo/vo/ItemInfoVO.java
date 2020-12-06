package top.candysky.pojo.vo;

import lombok.Data;
import top.candysky.pojo.Items;
import top.candysky.pojo.ItemsImg;
import top.candysky.pojo.ItemsParam;
import top.candysky.pojo.ItemsSpec;

import java.util.List;

@Data
public class ItemInfoVO {
    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;
}
