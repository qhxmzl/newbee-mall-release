package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import javax.servlet.http.HttpSession;

public interface UserService {
    // 后台分页
    PageResult getUserPage(PageQueryUtil pageQueryUtil);

    // 用户禁用与解除禁用(0-未锁定 1-已锁定)
    Boolean lockUsers(Integer[] ids,int lockStatus);

    //前台登录
    String login(String loginName, String passwordMD5, HttpSession httpSession);

    //前台用户注册
    String register(String loginName,String password);

    // 前台个人中心修改用户信息
    NewBeeMallUserVO updateUserInfo(User user,HttpSession httpSession);
}
