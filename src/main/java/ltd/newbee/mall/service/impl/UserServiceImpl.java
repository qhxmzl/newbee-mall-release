package ltd.newbee.mall.service.impl;


import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.dao.UserMapper;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.service.UserService;
import ltd.newbee.mall.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    // 分页列表
    @Override
    public PageResult getUserPage(PageQueryUtil pageQueryUtil) {
        List<User> userList = userMapper.findUserList(pageQueryUtil);
        int totalUser = userMapper.getTotalUser(pageQueryUtil);
        PageResult pageResult = new PageResult(userList,totalUser, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    //用户禁用与解除禁用(0-未锁定 1-已锁定)
    @Override
    public Boolean lockUsers(Integer[] ids,int lockStatus) {
        if(ids.length < 1){
            return false;
        }
        return userMapper.lockUserBatch(ids, lockStatus) > 0;
    }

    //前台登录
    @Override
    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
        //登录:根据用户名和密码查询用户是否存在
        User user = userMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if(user != null && httpSession != null){
            if(user.getLockedFlag() == 1){
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            //昵称太长 影响页面展示
            if(user.getNickName() != null && user.getNickName().length() > 7){
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            NewBeeMallUserVO newBeeMallUserVO = new NewBeeMallUserVO();
            BeanUtil.copyProperties(user, newBeeMallUserVO);
            //设置购物车中的数量
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, newBeeMallUserVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }


    //前台用户注册
    @Override
    public String register(String loginName, String password) {
        //判断此用户名是否已被注册
        if(userMapper.selectByLoginName(loginName) != null){
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        User registerUser = new User();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        String passwordMD5 = MD5Util.MD5Encode(password, "utf-8");
        registerUser.setPasswordMd5(passwordMD5);
        //前台注册：添加用户
        if(userMapper.insertSelective(registerUser) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }


    // 前台个人中心修改用户信息
    @Override
    public NewBeeMallUserVO updateUserInfo(User user, HttpSession httpSession) {
        NewBeeMallUserVO userTemp = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        //前台个人中心修改用户信息：根据id查询用户信息
        User userFromDB = userMapper.selectByPrimaryKey(userTemp.getUserId());
        if(userFromDB != null){
            if(!StringUtils.isEmpty(user.getNickName())){
                userFromDB.setNickName(NewBeeMallUtils.cleanString(user.getNickName()));
            }
            if(!StringUtils.isEmpty(user.getIntroduceSign())){
                userFromDB.setIntroduceSign(NewBeeMallUtils.cleanString(user.getIntroduceSign()));
            }
            if(!StringUtils.isEmpty(user.getAddress())){
                userFromDB.setAddress(NewBeeMallUtils.cleanString(user.getAddress()));
            }
            // 前台个人中心修改用户信息
            if(userMapper.updateByPrimaryKeySelective(userFromDB) > 0){
                NewBeeMallUserVO newBeeMallUserVO = new NewBeeMallUserVO();
                userFromDB = userMapper.selectByPrimaryKey(user.getUserId());
                BeanUtil.copyProperties(userFromDB, newBeeMallUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY,newBeeMallUserVO);
                return newBeeMallUserVO;
            }
        }
        return null;
    }
}
