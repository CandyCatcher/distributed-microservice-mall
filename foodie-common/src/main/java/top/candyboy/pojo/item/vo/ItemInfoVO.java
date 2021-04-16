package top.candyboy.pojo.item.vo;

import lombok.Data;
import top.candyboy.pojo.item.Items;
import top.candyboy.pojo.item.ItemsImg;
import top.candyboy.pojo.item.ItemsParam;
import top.candyboy.pojo.item.ItemsSpec;

import java.util.List;

@Data
public class ItemInfoVO {
    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;
}
