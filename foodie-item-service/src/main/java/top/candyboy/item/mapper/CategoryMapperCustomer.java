package top.candyboy.item.mapper;

import org.apache.ibatis.annotations.Param;
import top.candyboy.item.pojo.Category;
import top.candyboy.item.pojo.vo.CategoryVO;
import top.candyboy.item.pojo.vo.NewItemsVO;

import java.util.List;
import java.util.Map;

public interface CategoryMapperCustomer extends MyMapper<Category> {

    public List<CategoryVO> getSubCatList(Integer rootCatID);

    public List<NewItemsVO> getSixNewItemsLazy(@Param("paramsMap") Map<String, Object> map);
}