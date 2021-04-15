package top.candyboy.item.pojo.vo;

import lombok.Data;
import top.candyboy.item.pojo.Items;
import top.candyboy.item.pojo.ItemsImg;
import top.candyboy.item.pojo.ItemsParam;
import top.candyboy.item.pojo.ItemsSpec;

import java.util.List;

@Data
public class ItemInfoVO {
    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;
}
