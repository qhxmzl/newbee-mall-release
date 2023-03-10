package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarouselMapper {

    // 查询分页后这一页的数据
    List<Carousel> findCarouselList(PageQueryUtil pageQueryUtil);

    //查询总记录数
    int getTotalCarousels(PageQueryUtil pageQueryUtil);

    //新增
    int insertSelective(Carousel carousel);

    //主键获取详情
    Carousel selectByPrimaryKey(Integer carouselId);
    //修改
    int updateByPrimaryKeySelective(Carousel carousel);


    //删除
    int deleteBatch(Integer[] ids);


    // 返回固定数量的轮播图对象(首页调用)
    List<Carousel> findCarouselsByNum(@Param("number") int number);
}
