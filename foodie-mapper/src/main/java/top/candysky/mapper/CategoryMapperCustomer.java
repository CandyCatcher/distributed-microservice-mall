package top.candysky.mapper;

import org.apache.ibatis.annotations.Param;
import top.candysky.my.mapper.MyMapper;
import top.candysky.pojo.Category;
import top.candysky.pojo.vo.CategoryVO;
import top.candysky.pojo.vo.NewItemsVO;

import java.util.List;
import java.util.Map;

public interface CategoryMapperCustomer extends MyMapper<Category> {

    public List<CategoryVO> getSubCatList(Integer rootCatID);

    public List<NewItemsVO> getSixNewItemsLazy(@Param("paramsMap") Map<String, Object> map);
}