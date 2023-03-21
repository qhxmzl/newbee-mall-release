package ltd.newbee.mall.service.impl;


import ltd.newbee.mall.common.*;
import ltd.newbee.mall.controller.vo.*;
import ltd.newbee.mall.dao.GoodInfoMapper;
import ltd.newbee.mall.dao.OrderItemMapper;
import ltd.newbee.mall.dao.OrderMapper;
import ltd.newbee.mall.dao.ShoppingCartMapper;
import ltd.newbee.mall.entity.GoodInfo;
import ltd.newbee.mall.entity.Order;
import ltd.newbee.mall.entity.OrderItem;
import ltd.newbee.mall.entity.StockNumDTO;
import ltd.newbee.mall.service.OrderService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.NumberUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private GoodInfoMapper goodInfoMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 后台管理员订单列表
     */
    //分页列表
    @Override
    public PageResult getOrderPage(PageQueryUtil pageQueryUtil) {
        List<Order> orderList = orderMapper.findOrderList(pageQueryUtil);
        int totalOrder = orderMapper.getTotalOrder(pageQueryUtil);
        PageResult pageResult = new PageResult(orderList, totalOrder, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    //订单信息修改
    @Override
    @Transactional
    public String updateOrderInfo(Order order) {
        // 根据id查询详情
        Order temp = orderMapper.selectByPrimaryKey(order.getOrderId());
        //查询结果不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if(temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3){
            temp.setTotalPrice(order.getTotalPrice());
            temp.setUserAddress(order.getUserAddress());
            temp.setUpdateTime(new Date());
            // 调用修改方法
            if(orderMapper.updateByPrimaryKeySelective(temp) > 0){
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    // 主键获取详情
    @Override
    public List<NewBeeMallOrderItemVO> getOrderItems(Long id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        if(order != null){
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getOrderId());
            //获取订单数据
            if(!CollectionUtils.isEmpty(orderItems)){
                List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, NewBeeMallOrderItemVO.class);
                return newBeeMallOrderItemVOS;
            }
        }
        return null;
    }

    //配货
    @Override
    @Transactional  //事务：同成功或同失败
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改时间和更新时间
        List<Order> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if(!CollectionUtils.isEmpty(orders)){
            for (Order order : orders) {
                if(order.getIsDeleted() == 1){
                    errorOrderNos += order.getOrderNo() + " ";
                    continue;
                }
                if(order.getOrderStatus() != 1){
                    errorOrderNos += order.getOrderNo() + " ";
                }
            }
            if(StringUtils.isEmpty(errorOrderNos)){
                // // 配货:订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if(orderMapper.checkDone(Arrays.asList(ids)) > 0){
                    return ServiceResultEnum.SUCCESS.getResult();
                }else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            }else {
                //订单此时不可以执行出库操作
                if(errorOrderNos.length() > 0 && errorOrderNos.length() < 100){
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                }else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //为查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    // 出库
    @Override
    @Transactional //事务：同成功或同失败
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改时间和更新时间
        List<Order> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (Order order : orders) {
                // isDeleted=1 一定为已关闭订单
                if (order.getIsDeleted() == 1) {
                    errorOrderNos += order.getOrderNo() + " ";
                    continue;
                }
                if (order.getOrderStatus() != 1 && order.getOrderStatus() != 2) {
                    errorOrderNos += order.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //出库:订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (orderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可以执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }


    // 关闭订单
    @Override
    @Transactional //事务：同成功或同失败
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改时间和更新时间
        List<Order> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (Order order : orders) {
                // isDeleted=1 一定为已关闭订单
                if (order.getIsDeleted() == 1) {
                    errorOrderNos += order.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (order.getOrderStatus() == 4 && order.getOrderStatus() < 0) {
                    errorOrderNos += order.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //关闭订单:订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (orderMapper.closeOrder(Arrays.asList(ids), NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可以执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }


    /**
     * 前台用户订单列表
     */
    // 我的订单列表
    @Override
    public PageResult getMyOrders(PageQueryUtil pageQueryUtil) {
        // 前台我的订单数
        int total = orderMapper.getTotalNewBeeMallOrders(pageQueryUtil);
        //前台我的订单列表
        List<Order> newBeeMallOrders = orderMapper.findNewBeeMallOrderList(pageQueryUtil);
        List<NewBeeMallOrderListVO> orderListVOS = new ArrayList<>();
        if(total > 0){
            //数据转换 将尸体类转为vo
            orderListVOS = BeanUtil.copyList(newBeeMallOrders, NewBeeMallOrderListVO.class);
            //设置订单状态中文显示值
            for (NewBeeMallOrderListVO newBeeMallOrderListVO : orderListVOS) {
                newBeeMallOrderListVO.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(newBeeMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = newBeeMallOrders.stream().map(Order::getOrderId).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(orderIds)){
                // 根据订单ids获取订单项列表
                List<OrderItem> orderItems = orderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<OrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(OrderItem::getOrderId));
                for (NewBeeMallOrderListVO newBeeMallOrderListVO:orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if(itemByOrderIdMap.containsKey(newBeeMallOrderListVO.getOrderId())){
                        List<OrderItem> orderItemListTemp = itemByOrderIdMap.get(newBeeMallOrderListVO.getOrderId());
                        //将OrderItem对象列表转换为NewBeeMallOrderItemVO对象列表
                        List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, NewBeeMallOrderItemVO.class);
                        newBeeMallOrderListVO.setNewBeeMallOrderItemVOS(newBeeMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS,total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }


    // 获取订单详情
    @Override
    public NewBeeMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        Order newBeeMallOrder = orderMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(newBeeMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, NewBeeMallOrderItemVO.class);
                NewBeeMallOrderDetailVO newBeeMallOrderDetailVO = new NewBeeMallOrderDetailVO();
                BeanUtil.copyProperties(newBeeMallOrder, newBeeMallOrderDetailVO);
                newBeeMallOrderDetailVO.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(newBeeMallOrderDetailVO.getOrderStatus()).getName());
                newBeeMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(newBeeMallOrderDetailVO.getPayType()).getName());
                newBeeMallOrderDetailVO.setNewBeeMallOrderItemVOS(newBeeMallOrderItemVOS);
                return newBeeMallOrderDetailVO;
            }
        }
        return null;
    }


    // 保存订单并返回订单号
    @Override
    @Transactional
    public String saveOrder(NewBeeMallUserVO user, List<NewBeeMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(NewBeeMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(NewBeeMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<GoodInfo> newBeeMallGoods = goodInfoMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<GoodInfo> goodsListNotSelling = newBeeMallGoods.stream()
                .filter(newBeeMallGoodsTemp -> newBeeMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(goodsListNotSelling)){
            //goodsListNotSelling 对象非空则表示有下架商品
            NewBeeMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long,GoodInfo> newBeeMallGoodsMap = newBeeMallGoods.stream().collect(Collectors.toMap(GoodInfo::getGoodsId, Function.identity(),(entity1, entity2) -> entity1));
        //判断商品库存
        for (NewBeeMallShoppingCartItemVO shoppingCartItemVO: myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if(!newBeeMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())){
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if(shoppingCartItemVO.getGoodsCount() > newBeeMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()){
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if(!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(newBeeMallGoods)){
            if(shoppingCartMapper.deleteBatch(itemIdList) > 0){
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                // 库存修改
                int updateStockNumResult = goodInfoMapper.updateStockNum(stockNumDTOS);
                if(updateStockNumResult < 1){
                    NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                Order newBeeMallOrder = new Order();
                newBeeMallOrder.setOrderNo(orderNo);
                newBeeMallOrder.setUserId(user.getUserId());
                newBeeMallOrder.setUserAddress(user.getAddress());
                //总价
                for (NewBeeMallShoppingCartItemVO newBeeMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += newBeeMallShoppingCartItemVO.getGoodsCount() * newBeeMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    NewBeeMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                newBeeMallOrder.setTotalPrice(priceTotal);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                newBeeMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (orderMapper.insertSelective(newBeeMallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<OrderItem> newBeeMallOrderItems = new ArrayList<>();
                    for (NewBeeMallShoppingCartItemVO newBeeMallShoppingCartItemVO : myShoppingCartItems) {
                        OrderItem newBeeMallOrderItem = new OrderItem();
                        //使用BeanUtil工具类将newBeeMallShoppingCartItemVO中的属性复制到newBeeMallOrderItem对象中
                        BeanUtil.copyProperties(newBeeMallShoppingCartItemVO, newBeeMallOrderItem);
                        //NewBeeMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        newBeeMallOrderItem.setOrderId(newBeeMallOrder.getOrderId());
                        newBeeMallOrderItems.add(newBeeMallOrderItem);
                    }
                    //保存至数据库
                    if (orderItemMapper.insertBatch(newBeeMallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    NewBeeMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                NewBeeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            NewBeeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }


    // 手动取消订单
    @Override
    public String cancelOrder(String orderNo, Long userId) {
        //通过订单号查询用户对象
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null){
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            if(orderMapper.closeOrder(Collections.singletonList(order.getOrderId()),NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()) > 0){
                return ServiceResultEnum.SUCCESS.getResult();
            }else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }


    //确认收货,完成订单
    @Override
    public String finishOrder(String orderNo, Long userId) {
        //通过订单号查询用户对象
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null){
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            order.setOrderStatus((byte) NewBeeMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            order.setUpdateTime(new Date());
            //修改订单状态
            if(orderMapper.updateByPrimaryKeySelective(order) > 0){
                return ServiceResultEnum.SUCCESS.getResult();
            }else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }


    // 获取订单详情
    @Override
    public Order getNewBeeMallOrderByOrderNo(String orderNo) {
        // 通过订单号查询用户对象
        return orderMapper.selectByOrderNo(orderNo);
    }


    // 支付成功
    @Override
    public String paySuccess(String orderNo, int payType) {
        //通过订单号查询用户对象
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null){
            //todo 订单状态判断 非待支付状态下不进行修改操作
            order.setOrderStatus((byte) NewBeeMallOrderStatusEnum.OREDER_PAID.getOrderStatus());
            order.setPayType((byte) payType);
            order.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            order.setPayTime(new Date());
            order.setUpdateTime(new Date());
            //修改订单状态：支付成功
            if(orderMapper.updateByPrimaryKeySelective(order) > 0){
                return ServiceResultEnum.SUCCESS.getResult();
            }else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

}
