package top.candyboy.item.service;

import top.candyboy.item.pojo.Category;
import top.candyboy.item.pojo.vo.CategoryVO;
import top.candyboy.item.pojo.vo.NewItemsVO;

import java.util.List;

public interface CategoryService {

    public List<Category> queryAllRootLevelCat();

    public List<CategoryVO> getSubCatList(Integer rootCatId);

    public List<NewItemsVO> getSixNewItems(Integer rootCatId);

}
