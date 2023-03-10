package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.ShoppingCart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShoppingCartMapper {

    // 通过用户ID获取我的购物车中的列表数据
    List<ShoppingCart> selectByUserId(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("number")int number);

    // 删除购物项
    int deleteBatch(List<Long> ids);

    ShoppingCart selectByUserIdAndGoodsId(@Param("userId") Long userId,@Param("goodsId") Long goodsId);

    ShoppingCart selectByPrimaryKey(Long cartItemId);

    // 修改购物车中的属性
    int updateByPrimaryKeySelective(ShoppingCart record);

    int selectCountByUserId(Long newBeeMallUserId);

    // 前台图片保存商品至购物车中
    int insertSelective(ShoppingCart record);

    // 删除购物车中的商品
    int deleteByPrimaryKey(Long cartItemId);
}
