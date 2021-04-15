package top.candyboy.item.service;

import top.candyboy.item.pojo.Carousel;

import java.util.List;

public interface CarouselService {

    public List<Carousel> queryAll(Integer isShow);

}
