package top.candysky.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.candysky.mapper.CarouselMapper;
import top.candysky.pojo.Carousel;
import top.candysky.service.CarouselService;

import java.util.List;

@Service
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
