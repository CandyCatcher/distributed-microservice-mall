package top.candyboy.facade.item;

import top.candyboy.pojo.item.Carousel;

import java.util.List;

public interface CarouselService {

    public List<Carousel> queryAll(Integer isShow);

}
