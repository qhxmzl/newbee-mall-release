package ltd.newbee.mall.service.impl;


import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.dao.GoodInfoMapper;
import ltd.newbee.mall.dao.ShoppingCartMapper;
import ltd.newbee.mall.entity.GoodInfo;
import ltd.newbee.mall.entity.ShoppingCart;
import ltd.newbee.mall.service.ShoppingCartService;
import ltd.newbee.mall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private GoodInfoMapper goodInfoMapper;

    // 根据当前用户ID获取我的购物车中的列表数据
    @Override
    public List<NewBeeMallShoppingCartItemVO> getMyShoppingCartItems(Long newBeeMalUserId) {
        List<NewBeeMallShoppingCartItemVO> newBeeMallShoppingCartItemVOS = new ArrayList<>();
        // 通过用户ID查询数据库购物车表中的列表数据
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByUserId(newBeeMalUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if(!CollectionUtils.isEmpty(shoppingCarts)){
            //查询商品信息并做数据转换
            // Collections是一个包装类,包含了很多静态方法,不能被实例化,就像一个工具类,比如提供的排序方法: Collections. sort(list)。
            // stream().map()提取List对象的某一列值。然后可以通过该对象进行循环输出商品的id存入list列表
            List<Long> GoodsIds = shoppingCarts.stream().map(ShoppingCart::getGoodsId).collect(Collectors.toList());
            //根据购物车中商品的id数组，获取商品表中的数据列表
            List<GoodInfo> goodInfos = goodInfoMapper.selectByPrimaryKeys(GoodsIds);
            Map<Long,GoodInfo> goodInfoMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(goodInfos)){
                // list转map
                goodInfoMap = goodInfos.stream().collect(Collectors.toMap(GoodInfo::getGoodsId, Function.identity(),(entity1,entity2)->entity1));
            }
            for (ShoppingCart shoppingCart:shoppingCarts) {
                NewBeeMallShoppingCartItemVO newBeeMallShoppingCartItemVO = new NewBeeMallShoppingCartItemVO();
                BeanUtil.copyProperties(shoppingCart, newBeeMallShoppingCartItemVO);
                // 判断是否包含指定的键名
                if(goodInfoMap.containsKey(shoppingCart.getGoodsId())){
                    GoodInfo goodInfo = goodInfoMap.get(shoppingCart.getGoodsId());
                    newBeeMallShoppingCartItemVO.setGoodsCoverImg(goodInfo.getGoodsCoverImg());
                    String goodName = goodInfo.getGoodsName();
                    //字符产过长导致文字超出的问题
                    if(goodName.length() > 28){
                        goodName = goodName.substring(0, 28) + "...";
                    }
                    newBeeMallShoppingCartItemVO.setGoodsName(goodName);
                    newBeeMallShoppingCartItemVO.setSellingPrice(goodInfo.getSellingPrice());
                    newBeeMallShoppingCartItemVOS.add(newBeeMallShoppingCartItemVO);
                }
            }
        }
        return newBeeMallShoppingCartItemVOS;
    }

    // 前台图片保存商品至购物车中
    @Override
    public String saveNewBeeMallCartItem(ShoppingCart shoppingCart) {
        ShoppingCart temp = shoppingCartMapper.selectByUserIdAndGoodsId(shoppingCart.getUserId(), shoppingCart.getGoodsId());
        if(temp != null){
            //已存在则修改记录
            temp.setGoodsCount(shoppingCart.getGoodsCount());
            return updateNewBeeMallCartItem(temp);
        }
        GoodInfo newBeeMallGoods = goodInfoMapper.selectByPrimaryKey(shoppingCart.getGoodsId());
        //商品为空
        if (newBeeMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = shoppingCartMapper.selectCountByUserId(shoppingCart.getUserId()) + 1;
        //超出单个商品的最大数量
        if (shoppingCart.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        //保存记录
        // 前台图片保存商品至购物车中
        if (shoppingCartMapper.insertSelective(shoppingCart) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }


    // 修改购物车中的属性
    @Override
    public String updateNewBeeMallCartItem(ShoppingCart newBeeMallShoppingCartItem) {
        ShoppingCart newBeeMallShoppingCartItemUpdate = shoppingCartMapper.selectByPrimaryKey(newBeeMallShoppingCartItem.getCartItemId());
        if (newBeeMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if (newBeeMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //todo 数量相同不会进行修改
        //todo userId不同不能修改
        newBeeMallShoppingCartItemUpdate.setGoodsCount(newBeeMallShoppingCartItem.getGoodsCount());
        newBeeMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录： 修改购物车中的属性:数量
        if (shoppingCartMapper.updateByPrimaryKeySelective(newBeeMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }


    // 删除购物车中的商品
    @Override
    public Boolean deleteById(Long shoppingCartId) {
        //todo userId不同不能删除
        return shoppingCartMapper.deleteByPrimaryKey(shoppingCartId) > 0;
    }
}
