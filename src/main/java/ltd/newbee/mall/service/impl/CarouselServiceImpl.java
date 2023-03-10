package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallIndexCarouselVO;
import ltd.newbee.mall.dao.CarouselMapper;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.service.CarouselService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ltd.newbee.mall.service.CarouselService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CarouselServiceImpl implements CarouselService {
    @Autowired
    private CarouselMapper carouselMapper;

    // 轮播图分页数据
    @Override
    public PageResult getCarouselPage(PageQueryUtil pageQueryUtil) {
        List<Carousel> carouselList = carouselMapper.findCarouselList(pageQueryUtil);
        int totalCarousels = carouselMapper.getTotalCarousels(pageQueryUtil);

        PageResult pageResult = new PageResult(carouselList, totalCarousels, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public String insertSelective(Carousel carousel) {
        if(carouselMapper.insertSelective(carousel) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }


    //修改
    @Override
    public String updateCarousel(Carousel carousel) {
        Carousel carousel1 = carouselMapper.selectByPrimaryKey(carousel.getCarouselId());
        if(carousel1 == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        carousel1.setCarouselRank(carousel.getCarouselRank());
        carousel1.setRedirectUrl(carousel.getRedirectUrl());
        carousel1.setCarouselUrl(carousel.getCarouselUrl());
        carousel1.setUpdateTime(new Date());
        if(carouselMapper.updateByPrimaryKeySelective(carousel1) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }


    //主键获取详情
    @Override
    public Carousel getCarouselById(Integer id) {
        return carouselMapper.selectByPrimaryKey(id);
    }

    //删除
    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if(ids.length < 1){
            return false;
        }
        return carouselMapper.deleteBatch(ids) > 0;
    }

    // 返回固定数量的轮播图对象(首页调用)
    @Override
    public List<NewBeeMallIndexCarouselVO> getCarouselsForIndex(int number) {
        List<NewBeeMallIndexCarouselVO> newBeeMallIndexCarouselVOS = new ArrayList<>(number);
        List<Carousel> carousels = carouselMapper.findCarouselsByNum(number);
        if(!CollectionUtils.isEmpty(carousels)){
            newBeeMallIndexCarouselVOS = BeanUtil.copyList(carousels, NewBeeMallIndexCarouselVO.class);
        }
        return newBeeMallIndexCarouselVOS;
    }

}
