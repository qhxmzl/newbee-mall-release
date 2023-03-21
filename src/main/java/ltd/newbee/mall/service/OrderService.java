package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.NewBeeMallOrderDetailVO;
import ltd.newbee.mall.controller.vo.NewBeeMallOrderItemVO;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.Order;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;

public interface OrderService {
    /**
     * 后台管理员订单列表
     * @param pageQueryUtil
     * @return
     */
    // 分页列表
    PageResult getOrderPage(PageQueryUtil pageQueryUtil);

    //订单信息修改
    String updateOrderInfo(Order order);

    // 主键获取详情
    List<NewBeeMallOrderItemVO> getOrderItems(Long id);

    //配货
    String checkDone(Long[] ids);

    // 出库
    String checkOut(Long[] ids);

    // 关闭订单
    String closeOrder(Long[] ids);

    /**
     * 前台用户订单列表
     */
    // 我的订单列表
    PageResult getMyOrders(PageQueryUtil pageQueryUtil);

    // 获取订单详情
    NewBeeMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    //保存订单并返回订单号
    String saveOrder(NewBeeMallUserVO user, List<NewBeeMallShoppingCartItemVO> myShoppingCartItems);

    // 手动取消订单
    String cancelOrder(String orderNo,Long userId);

    //确认收货,完成订单
    String finishOrder(String orderNo,Long userId);

    // 获取订单详情
    Order getNewBeeMallOrderByOrderNo(String orderNo);

    // 支付成功
    String paySuccess(String orderNo,int payType);
}

