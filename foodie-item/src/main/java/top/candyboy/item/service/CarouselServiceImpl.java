package top.candyboy.item.service;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.candyboy.item.CarouselService;
import top.candyboy.item.mapper.CarouselMapper;
import top.candyboy.pojo.item.Carousel;

import java.util.List;

@DubboService(interfaceClass = CarouselService.class)
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Carousel> queryAll(Integer isShow) {

        Example carouselExample = new Example(Carousel.class);
        carouselExample.orderBy("sort");
        Example.Criteria criteria = carouselExample.createCriteria();
        criteria.andEqualTo("isShow", isShow);
        return carouselMapper.selectByExample(carouselExample);
    }
}
