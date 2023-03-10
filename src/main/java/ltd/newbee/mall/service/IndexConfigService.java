package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.NewBeeMallIndexConfigGoodsVO;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;

public interface IndexConfigService {
    // 获取后台商品配置：列表
    PageResult getIndexConfigPage(PageQueryUtil pageQueryUtil);

    //后台商品配置：新增
    String  insertSelective(IndexConfig indexConfig);

    // 后台商品配置：修改
    String updateConfigPage(IndexConfig indexConfig);

    // 后台商品配置： 详情
    IndexConfig getIndexConfigById(Long id);

    //主键获取详情
    IndexConfig getConfigPageById(Long id);

    //后台商品配置：批量删除
    Boolean deleteBatch(Integer[] ids);

    // 返回固定数量的首页配置商品对象(首页调用)
    List<NewBeeMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType,int number);
}
