package top.candyboy.pojo.item.vo;

import lombok.Data;
import top.candyboy.facade.item.pojo.Items;
import top.candyboy.facade.item.pojo.ItemsImg;
import top.candyboy.facade.item.pojo.ItemsParam;
import top.candyboy.facade.item.pojo.ItemsSpec;

import java.util.List;

@Data
public class ItemInfoVO {
    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;
}
