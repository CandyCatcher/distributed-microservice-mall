package top.candyboy.facade.item.service;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.candyboy.enums.CategoryType;
import top.candyboy.facade.item.CategoryService;
import top.candyboy.facade.item.mapper.CategoryMapper;
import top.candyboy.facade.item.mapper.CategoryMapperCustomer;
import top.candyboy.pojo.item.Category;
import top.candyboy.pojo.item.vo.CategoryVO;
import top.candyboy.pojo.item.vo.NewItemsVO;

import java.util.HashMap;
import java.util.List;

@DubboService(interfaceClass = CategoryService.class)
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
