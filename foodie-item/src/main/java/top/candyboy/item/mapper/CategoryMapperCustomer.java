package top.candyboy.item.mapper;

import org.apache.ibatis.annotations.Param;
import top.candyboy.pojo.item.Category;
import top.candyboy.pojo.item.vo.CategoryVO;
import top.candyboy.pojo.item.vo.NewItemsVO;

import java.util.List;
import java.util.Map;

public interface CategoryMapperCustomer extends MyMapper<Category> {

    public List<CategoryVO> getSubCatList(Integer rootCatID);

    public List<NewItemsVO> getSixNewItemsLazy(@Param("paramsMap") Map<String, Object> map);
}