package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.AdminUser;
import ltd.newbee.mall.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//后台登录注册/修改
/**
 * 只需一次登录，如果登录过，下一次再访问的时候就无需再次进行登录拦截，可以直接访问网站里面的内容了。
 *
 * 在正确登录之后，就将user保存到session中，再次访问页面的时候，
 * 登录拦截器就可以找到这个user对象，就不需要再次拦截到登录界面了.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminUserService adminUserService;

    //从后端地址跳转到前端页面
    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    //登录
    /**
     *@RequestParam用于接收url地址传参，如果发送非json格式数据，选用@RequestParam接收请求参数
     *@RequestBody用于接收json数据
     *Session是存储在服务端(安全)；而Cookie是存储在客户端（易被窃取）
     */
    @PostMapping("/login")
    public String login(@RequestParam("userName") String username, @RequestParam("password") String password,
                    @RequestParam("verifyCode") String verifyCode ,HttpSession session){
        if(StringUtils.isEmpty(verifyCode)){
            session.setAttribute("errorMsg", "验证码不能为空");
            return "admin/login";
        }
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            session.setAttribute("errorMsg", "用户名和密码不能为空");     //后台密码默认123456
            return "admin/login";
        }
        String kaptchaCode = session.getAttribute("verifyCode") + "";
        if(StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)){
            session.setAttribute("errorMsg", "验证码错误");
            return "admin/login";
        }
        AdminUser adminUser = adminUserService.login(username, password);
        if(adminUser != null){
            session.setAttribute("loginUser", adminUser.getNickName());
            session.setAttribute("loginUserId", adminUser.getAdminUserId());
            //重定向到新的页面，地址栏发生变化
            return "redirect:/admin/index";
        }else {
            session.setAttribute("errorMsg","登陆失败");
        }
        return "admin/login";
    }

    //从后端地址跳转到前端页面
    @GetMapping({"/index","/","","/index.html"})
    public String index(HttpServletRequest request) {
        request.setAttribute("path","index");
        return "admin/index";
    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request){
        Integer loginUserId = (Integer) request.getSession().getAttribute("loginUserId");
        AdminUser adminUser = adminUserService.getUserDetailById(loginUserId);
        if(adminUser == null){
            return "admin/login";
        }
        request.setAttribute("path","profile");
        request.setAttribute("loginUserName",adminUser.getLoginUserName());
        request.setAttribute("nickName",adminUser.getNickName());
        return "admin/profile";
    }

    //修改用户名和昵称
    @PostMapping("/profile/name")
    @ResponseBody
    public String nameUpdate(HttpServletRequest request,@RequestParam("loginUserName")String loginUserName, @RequestParam("nickName")String nickName){
        //修改用户名昵称
        if(StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)){
            return "参数不能为空";
        }
        Integer loginUserId = (Integer) request.getSession().getAttribute("loginUserId");
        if(adminUserService.updateName(loginUserId,loginUserName,nickName)){
            return ServiceResultEnum.SUCCESS.getResult();
        }else {
            return "修改失败";
        }
    }

    //修改密码
    @PostMapping("/profile/password")
    @ResponseBody
    public String passwordUpdate(HttpServletRequest request,@RequestParam("originalPassword") String originalPassword,@RequestParam("newPassword") String newPassword){
        if(StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)){
            return "参数不能为空";
        }
        Integer loginUserId = (Integer) request.getSession().getAttribute("loginUserId");
        if(adminUserService.updatePassword(loginUserId,originalPassword,newPassword)){
            //修改成功后清空session中的数据，前端控制跳转至登录页
            request.getSession().removeAttribute("loginUserId");
            request.getSession().removeAttribute("loginUser");
            request.getSession().removeAttribute("errorMsg");
            return ServiceResultEnum.SUCCESS.getResult();
        }else {
            return "修改失败";
        }
    }


    //安全退出
    @GetMapping("/logout")
    public String exittologin(HttpServletRequest request) {
        request.getSession().removeAttribute("loginUserId");
        request.getSession().removeAttribute("loginUser");
        request.getSession().removeAttribute("errorMsg");
        return "admin/login";
    }
}
