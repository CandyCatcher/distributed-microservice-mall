package top.candysky.service;

import org.apache.ibatis.annotations.Param;
import top.candysky.pojo.Category;
import top.candysky.pojo.vo.CategoryVO;
import top.candysky.pojo.vo.NewItemVO;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    public List<Category> queryAllRootLevelCat();

    public List<CategoryVO> getSubCatList(Integer rootCatId);

    public List<NewItemVO> getSixNewItemsLazy(Integer rootCatId);

}
