package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.NewBeeMallIndexCarouselVO;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;

public interface CarouselService {
    // 轮播图分页数据
    PageResult getCarouselPage(PageQueryUtil pageQueryUtil);

    //新增
    String  insertSelective(Carousel carousel);

    // 修改
    String updateCarousel(Carousel carousel);

    //主键获取详情
    Carousel getCarouselById(Integer id);

    //删除
    Boolean deleteBatch(Integer[] ids);


    /**
     * 前台
     */
    //返回固定数量的轮播图对象(首页调用)
    List<NewBeeMallIndexCarouselVO> getCarouselsForIndex(int number);
}
