package org.jeff.service;

import org.jeff.pojo.Carousel;

import java.util.List;

public interface CarouselService {
    /**
     * 首页轮播图，查询所有图片
     */
    public List<Carousel> queryAll(Integer isShow);
    
}
