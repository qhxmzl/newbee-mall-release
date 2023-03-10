package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.Order;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    List<Order> findOrderList(PageQueryUtil pageQueryUtil);

    //查询总记录数
    int getTotalOrder(PageQueryUtil pageQueryUtil);

    Order selectByPrimaryKey(Long orderId);

    //修改
    int updateByPrimaryKeySelective(Order record);

    //配货：查询所有的订单 判断状态 修改状态和更新时间
    List<Order> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int updateByPrimaryKey(Order record);

    //出库:订单状态正常 可以执行出库操作 修改订单状态和更新时间
    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    // 配货:订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
    int checkDone(@Param("orderIds") List<Long> asList);

    /**
     * 前台用户订单列表
     */
    // 前台我的订单数
    int getTotalNewBeeMallOrders(PageQueryUtil pageQueryUtil);

    // 前台我的订单列表
    List<Order> findNewBeeMallOrderList(PageQueryUtil pageQueryUtil);

    //通过订单号查询用户对象
    Order selectByOrderNo(String orderNo);

    // 生成订单项并保存订单项纪录
    int insertSelective(Order record);
}

