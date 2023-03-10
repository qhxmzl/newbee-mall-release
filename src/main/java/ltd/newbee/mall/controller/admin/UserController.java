package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.service.UserService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String login(HttpServletRequest request) {
        request.setAttribute("path","users");
        return "admin/newbee_mall_user";
    }

    //列表
    @GetMapping("/users/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object> params){
        if(StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit")) ){
            return ResultGenerator.genFailResult("参数异常");
        }
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(userService.getUserPage(pageQueryUtil));
    }

    //解禁与禁用
    @PostMapping("/users/lock/{lockStatus}")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids,@PathVariable int lockStatus){
        if(ids.length < 1){
            return ResultGenerator.genFailResult("参数异常！");
        }
        if(lockStatus != 0 && lockStatus != 1){
            return ResultGenerator.genFailResult("操作非法！");
        }
        if(userService.lockUsers(ids,lockStatus)){
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("禁用失败");
        }
    }

}
