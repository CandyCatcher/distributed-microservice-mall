package top.candysky.service;

import top.candysky.pojo.Carousel;
import top.candysky.pojo.vo.CategoryVO;

import java.util.List;

public interface CarouselService {

    public List<Carousel> queryAll(Integer isShow);

}
