package top.candyboy.item.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.candyboy.enums.CategoryType;
import top.candyboy.item.mapper.CategoryMapper;
import top.candyboy.item.mapper.CategoryMapperCustomer;
import top.candyboy.item.pojo.Category;
import top.candyboy.item.pojo.vo.CategoryVO;
import top.candyboy.item.pojo.vo.NewItemsVO;
import top.candyboy.item.service.CategoryService;

import java.util.HashMap;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    CategoryMapperCustomer categoryMapperCustomer;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> queryAllRootLevelCat() {

        Example categoryExample = new Example(Category.class);
        Example.Criteria criteria = categoryExample.createCriteria();
        criteria.andEqualTo("type", CategoryType.ROOT.type);
        return categoryMapper.selectByExample(categoryExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVO> getSubCatList(Integer rootCatId) {
        return categoryMapperCustomer.getSubCatList(rootCatId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<NewItemsVO> getSixNewItems(Integer rootCatId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("rootCatId", rootCatId);
        return categoryMapperCustomer.getSixNewItemsLazy(map);
    }
}
