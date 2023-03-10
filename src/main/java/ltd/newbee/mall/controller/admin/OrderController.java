package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallOrderItemVO;
import ltd.newbee.mall.entity.Order;
import ltd.newbee.mall.service.OrderService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public String login(HttpServletRequest request) {
        request.setAttribute("path","orders");
        return "admin/newbee_mall_order";
    }

    // 分页列表
    @GetMapping("/orders/list")
    @ResponseBody
    public Result List(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(orderService.getOrderPage(pageQueryUtil));
    }

    // 修改
    @PostMapping("/orders/update")
    @ResponseBody
    public Result update(@RequestBody Order order){
        if(Objects.isNull(order.getTotalPrice())
        || Objects.isNull(order.getOrderId())
        || order.getOrderId() < 1
        || order.getTotalPrice() < 1
        || StringUtils.isEmpty(order.getUserAddress())){
            return ResultGenerator.genFailResult("参数异常");
        }
        String result = orderService.updateOrderInfo(order);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    // 详情
    @GetMapping("/order-items/{id}")
    @ResponseBody
    public Result Info(@PathVariable Long id){
        List<NewBeeMallOrderItemVO> orderItems = orderService.getOrderItems(id);
        if(!CollectionUtils.isEmpty(orderItems)){
            return ResultGenerator.genSuccessResult(orderItems);
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }


    //配货
    @PostMapping("/orders/checkDone")
    @ResponseBody
    public Result checkDone(@RequestBody Long[] ids){
        if(ids.length < 1){
            return ResultGenerator.genFailResult("参数异常");
        }
        // 配货：查询所有的订单 判断状态 修改状态和更新时间
        String result = orderService.checkDone(ids);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    //出库
    @PostMapping("/orders/checkOut")
    @ResponseBody
    public Result checkOut(@RequestBody Long[] ids){
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常");
        }
        String result = orderService.checkOut(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    // 关闭订单
    @PostMapping("/orders/close")
    @ResponseBody
    public Result closeOrder(@RequestBody Long[] ids){
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常");
        }
        String result = orderService.closeOrder(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }
}

