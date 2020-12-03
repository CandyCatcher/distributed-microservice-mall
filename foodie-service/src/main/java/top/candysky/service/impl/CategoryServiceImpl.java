package top.candysky.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.candysky.enums.CategoryType;
import top.candysky.mapper.CategoryMapper;
import top.candysky.mapper.CategoryMapperCustomer;
import top.candysky.pojo.Category;
import top.candysky.pojo.vo.CategoryVO;
import top.candysky.pojo.vo.NewItemVO;
import top.candysky.service.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<NewItemVO> getSixNewItemsLazy(Integer rootCatId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("rootCatId", rootCatId);
        return categoryMapperCustomer.getSixNewItems(map);
    }
}
