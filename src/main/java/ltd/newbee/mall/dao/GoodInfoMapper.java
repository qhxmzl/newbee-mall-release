package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.GoodInfo;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.entity.StockNumDTO;
import ltd.newbee.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;


import java.util.List;

public interface GoodInfoMapper {
    //后台商品管理，分页列表数据
    List<GoodInfo> findGoodInfoList(PageQueryUtil pageQueryUtil);

    //后台商品管理，查询总记录数
    int getTotalGoodInfo(PageQueryUtil pageQueryUtil);

    //后台商品管理，根据商品名查询一条分类数据
    GoodInfo selectByGoodsName(@Param("goodsName") String goodsName);

    //后台商品管理，新增商品
    int insertSelective(GoodInfo goodsInfo);

    // 后台商品管理，修改商品信息
    int updateByPrimaryKeySelective(GoodInfo goodsInfo);

    // 后台商品管理，获取商品详情
    GoodInfo selectByPrimaryKey(Long goodsId);

    //后台商品管理，批量修改销售状态(上架下架)
    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

    /**
     *  前台
     */
    //根据购物车中商品的id数组，获取商品表中的数据列表
    List<GoodInfo> selectByPrimaryKeys(List<Long> goodsIds);

    // 前台模糊商品搜索
    List<GoodInfo> findNewBeeMallGoodsListBySearch(PageQueryUtil pageQueryUtil);

    // 前台模糊商品总记录数
    int getTotalNewBeeMallGoodsBySearch(PageQueryUtil pageQueryUtil);

    // 库存修改
    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);
}
