package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsCategoryMapper {
    //分页列表数据
    List<GoodsCategory> findGoodsCategoryList(PageQueryUtil pageQueryUtil);

    //查询总记录数
    int getTotalGoodsCategory(PageQueryUtil pageQueryUtil);

    //后台"新增"商品分类：根据分类等级和分类名称，查询是否已经有该分类
    GoodsCategory selectByCategoryLevelAndCategoryName(@Param("categoryLevel") Byte categoryLevel,@Param("categoryName") String categoryName);
    //后台"新增"一条商品分类
    int insertSelective(GoodsCategory goodsCategory);

    //后台"修改"商品分类
    int updateByPrimaryKeySelective(GoodsCategory goodsCategory);

    //主键获取该分类详情
    GoodsCategory selectByPrimaryKey(Long goodsCategory);

    //删除商品分类
    int deleteBatch(Integer[] ids);

    // 根据parentId和level获取分类列表
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(@Param("parentIds") List<Long> parentIds, @Param("categoryLevel") int categoryLevel, @Param("number") int number);

}
