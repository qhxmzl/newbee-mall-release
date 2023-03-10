package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.NewBeeMallIndexCategoryVO;
import ltd.newbee.mall.controller.vo.SearchPageCategoryVO;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;

public interface GoodsCategoryService {
    //商品分类分页列表
    PageResult getGoodsCategoryPage(PageQueryUtil pageQueryUtil);

    //后台"新增"商品分类
    String insertSelective(GoodsCategory goodsCategory);

    // 后台"修改"商品分类
    String updateCategoryPage(GoodsCategory goodsCategory);

    //商品信息：有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
    GoodsCategory getGoodsCategoryById(Long id);

    //删除商品分类
    Boolean deleteBatch(Integer[] ids);


    /**
     * 前台
     * @return
     */
     //返回分类数据(首页调用)
    List<NewBeeMallIndexCategoryVO> getCategoriesForIndex();

    // 返回分类数据(搜索页调用)
    SearchPageCategoryVO getCategoriesForSearch(Long categoryId);

     // 根据parentId和level获取分类列表
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);
}
