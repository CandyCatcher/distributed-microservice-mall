package top.candysky.service;

import top.candysky.pojo.Category;
import top.candysky.pojo.vo.CategoryVO;
import top.candysky.pojo.vo.NewItemsVO;

import java.util.List;

public interface CategoryService {

    public List<Category> queryAllRootLevelCat();

    public List<CategoryVO> getSubCatList(Integer rootCatId);

    public List<NewItemsVO> getSixNewItems(Integer rootCatId);

}
