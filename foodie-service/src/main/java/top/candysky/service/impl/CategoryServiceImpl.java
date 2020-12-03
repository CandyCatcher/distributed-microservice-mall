package top.candysky.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.candysky.enums.CategoryType;
import top.candysky.mapper.CategoryMapper;
import top.candysky.pojo.Category;
import top.candysky.service.CategoryService;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public List<Category> queryAllRootLevelCat() {

        Example categoryExample = new Example(Category.class);
        Example.Criteria criteria = categoryExample.createCriteria();
        criteria.andEqualTo("type", CategoryType.ROOT.type);
        return categoryMapper.selectByExample(categoryExample);
    }
}
