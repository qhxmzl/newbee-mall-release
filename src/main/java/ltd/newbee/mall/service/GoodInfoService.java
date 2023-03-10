package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.GoodInfo;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;


public interface GoodInfoService {
    //后台商品管理：分页列表
    PageResult getGoodInfoPage(PageQueryUtil pageQueryUtil);


    //id获取商品详情
    GoodInfo getNewBeeMallGoodsById(Long id);

    //后台商品管理：添加商品
    String saveNewBeeMallGoods(GoodInfo goodInfo);

    /**
     *后台商品管理，修改商品信息
     */
    String updateNewBeeMallGoods(GoodInfo goods);

    /**
     * 后台商品管理，批量修改销售状态(上架下架)
     */
    Boolean batchUpdateSellStatus(Long[] ids,int sellStatus);

    // 前台模糊商品搜索
    PageResult searchNewBeeMallGoods(PageQueryUtil pageQueryUtil);
}
