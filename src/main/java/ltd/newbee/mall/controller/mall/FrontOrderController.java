package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallOrderDetailVO;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.Order;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.service.OrderService;
import ltd.newbee.mall.service.ShoppingCartService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

//前台订单
@Controller
public class FrontOrderController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderService orderService;


    //订单页
    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        // 获取订单详情
        NewBeeMallOrderDetailVO orderDetailVO = orderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        if (orderDetailVO == null) {
            return "error/error_5xx";
        }
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }

    //订单列表
    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession){
        //获取当前登录的用户对象
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if(StringUtils.isEmpty(params.get("page"))){
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult",orderService.getMyOrders(pageQueryUtil));
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }

    //添加订单
    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession httpSession){
        //获取当前登录的用户对象
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        // 根据当前用户ID获取我的购物车中的列表数据
        List<NewBeeMallShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        if(StringUtils.isEmpty(user.getAddress().trim())){
            //无收货地址
            NewBeeMallException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
        }
        if(CollectionUtils.isEmpty(myShoppingCartItems)){
            //购物车中无数据则跳转到错误页
            NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }
        //保存订单并返回订单号
        String saveOrderResult = orderService.saveOrder(user, myShoppingCartItems);
        //跳转到订单详情页
        return "redirect:/orders/"  + saveOrderResult;
    }

    //手动取消订单
    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo,HttpSession httpSession){
        //获取当前登录的用户对象
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        // 手动取消订单
        String cancelOrderResult = orderService.cancelOrder(orderNo, user.getUserId());
        if(ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    //确认收货,完成订单
    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo,HttpSession httpSession){
        //获取当前登录的用户对象
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        // 确认收货,完成订单
        String finishOrderResult = orderService.finishOrder(orderNo, user.getUserId());
        if(ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }


    // 获取订单详情，到付款页面
    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request,@RequestParam("orderNo") String orderNo,HttpSession httpSession){
        //获取当前登录的用户对象
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        // 获取订单详情
        Order order = orderService.getNewBeeMallOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        request.setAttribute("orderNo",orderNo);
        request.setAttribute("totalPrice", order.getTotalPrice());
        //到付款页面
        return "mall/pay-select";
    }

    //支付页面,选择支付方式
    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType) {
        //获取当前登录的用户对象
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        // 获取订单详情
        Order newBeeMallOrder = orderService.getNewBeeMallOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        request.setAttribute("orderNo",orderNo);
        request.setAttribute("totalPrice", newBeeMallOrder.getTotalPrice());
        if(payType == 1){
            return "mall/alipay";
        }else {
            return "mall/wxpay";
        }
    }

    //支付成功按钮
    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo,@RequestParam("payType") int payType){
        // 支付成功
        String payResult = orderService.paySuccess(orderNo, payType);
        if(ServiceResultEnum.SUCCESS.getResult().equals(payResult)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(payResult);
        }
    }
}
