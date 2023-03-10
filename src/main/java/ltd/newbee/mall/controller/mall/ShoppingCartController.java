package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.ShoppingCart;
import ltd.newbee.mall.service.ShoppingCartService;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request, HttpSession httpSession){
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        // 根据当前用户ID获取我的购物车中的列表数据
        List<NewBeeMallShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        //如果购物车中不空，则计算
        if(!CollectionUtils.isEmpty(myShoppingCartItems)){
            // 计算购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(NewBeeMallShoppingCartItemVO::getGoodsCount).sum();
            if(itemsTotal < 1 ){
                return "error/error_5xx";
            }
            //总价：计算每一个商品的数量乘以单价，得到该商品的总价，在累加为所有商品的总价
            for (NewBeeMallShoppingCartItemVO newBeeMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += newBeeMallShoppingCartItemVO.getGoodsCount() * newBeeMallShoppingCartItemVO.getSellingPrice();
            }
            if(priceTotal < 1){
                return "error/error_5xx";
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    // 前台图片保存商品至购物车中
    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveNewBeeMallShoppingCartItem(@RequestBody ShoppingCart shoppingCart, HttpSession httpSession){
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        shoppingCart.setUserId(user.getUserId());
        //判断数量
        // 前台图片保存商品至购物车中
        String saveResult = shoppingCartService.saveNewBeeMallCartItem(shoppingCart);
        //添加成功
        if(ServiceResultEnum.SUCCESS.getResult().equals(saveResult)){
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    // 修改购物车中的属性:数量
    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateNewBeeMallShoppingCartItem(@RequestBody ShoppingCart shoppingCart,HttpSession httpSession){
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        shoppingCart.setUserId(user.getUserId());
        //todo 判断数量: 修改购物车中的属性:数量
        String updateResult = shoppingCartService.updateNewBeeMallCartItem(shoppingCart);
        //修改成功
        if(ServiceResultEnum.SUCCESS.getResult().equals(updateResult)){
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }


    // 删除购物车中的商品
    @DeleteMapping("/shop-cart/{newBeeMallShoppingCartItemId}")
    @ResponseBody
    public Result updateNewBeeMallShoppingCartItem(@PathVariable("newBeeMallShoppingCartItemId") Long shoppingCartId,HttpSession httpSession){
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = shoppingCartService.deleteById(shoppingCartId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }


    //购物车内去结算页面
    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,HttpSession httpSession){
        int priceTotal = 0;
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        // 根据当前用户ID获取我的购物车中的列表数据
        List<NewBeeMallShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        if(CollectionUtils.isEmpty(myShoppingCartItems)){
            //无数据则不跳转至结算页
            return "/shop-cart";
        }else {
            //总价
            for (NewBeeMallShoppingCartItemVO newBeeMallShoppingCartItemVO:myShoppingCartItems) {
                priceTotal += newBeeMallShoppingCartItemVO.getGoodsCount() * newBeeMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }
}
