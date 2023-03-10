package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.service.UserService;
import ltd.newbee.mall.util.MD5Util;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// 登陆注册,个人中心
@Controller
public class PersonalController {
    @Autowired
    private UserService userService;



    //登录按钮
    @GetMapping({"/login","login.html"})
    public String loginPage(){
        return "mall/login";
    }

    //注册按钮
    @GetMapping({"/register","register.html"})
    public String registerPage(){
        return "mall/register";
    }

    //前台登录
    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestParam("loginName") String loginName, @RequestParam("password") String password, @RequestParam("verifyCode") String verifyCode,
                        HttpSession httpSession){
        if(StringUtils.isEmpty(loginName)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if(StringUtils.isEmpty(password)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if(StringUtils.isEmpty(verifyCode)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        String kaptchaCode = httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if(StringUtils.isEmpty(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }

        //todo 清verifyCode
        String loginResult = userService.login(loginName, MD5Util.MD5Encode(password, "utf-8"), httpSession);
        //登陆成功
        if(ServiceResultEnum.SUCCESS.getResult().equals(loginResult)){
            return ResultGenerator.genSuccessResult();
        }
        //登陆失败
        return ResultGenerator.genFailResult(loginResult);
    }

    //前台注册
    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("loginName") String loginName, @RequestParam("password") String password, @RequestParam("verifyCode") String verifyCode,
                           HttpSession httpSession){
        if(StringUtils.isEmpty(loginName)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if(StringUtils.isEmpty(password)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if(StringUtils.isEmpty(verifyCode)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        String kaptchaCode = httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if(StringUtils.isEmpty(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        //todo 清verifyCode
        String registerResult = userService.register(loginName,password);
        //注册成功
        if(ServiceResultEnum.SUCCESS.getResult().equals(registerResult)){
            return ResultGenerator.genSuccessResult();
        }
        //注册失败
        return ResultGenerator.genFailResult(registerResult);
    }


    //前台个人中心按钮
    @GetMapping("/personal")
    public String personalPage(HttpServletRequest request,HttpSession httpSession){
        request.setAttribute("path", "personal");
        return "mall/personal";
    }

    //前台个人中心修改用户信息
    @PostMapping("/personal/updateInfo")
    @ResponseBody
    public Result updateInfo(@RequestBody User user, HttpSession httpSession){
        // 前台个人中心修改用户信息
        NewBeeMallUserVO mallUserTemp = userService.updateUserInfo(user, httpSession);
        if(mallUserTemp == null){
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        }else {
            //返回成功
            Result result = ResultGenerator.genSuccessResult();
            return result;
        }
    }

    //个人中心退出登录
    @GetMapping("/logout")
    public String logout(HttpSession httpSession){
       httpSession.removeAttribute(Constants.MALL_USER_SESSION_KEY);
        return "mall/login";
    }

    @GetMapping("/personal/addresses")
    public String addressesPage() {
        return "mall/addresses";
    }
}
