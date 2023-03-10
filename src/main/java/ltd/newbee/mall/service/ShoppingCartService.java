package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    // 根据当前用户ID获取我的购物车中的列表数据
    List<NewBeeMallShoppingCartItemVO> getMyShoppingCartItems(Long newBeeMalUserId);


    //前台图片保存商品至购物车中
    String saveNewBeeMallCartItem(ShoppingCart shoppingCart);

    // 修改购物车中的属性：数量
    String updateNewBeeMallCartItem(ShoppingCart newBeeMallShoppingCartItem);

    //删除购物车中的商品
    Boolean deleteById(Long shoppingCartId);
}
