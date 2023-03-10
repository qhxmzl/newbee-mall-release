package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.AdminUser;
import org.apache.ibatis.annotations.Param;

public interface AdminUserService {
    // 登录方法
    AdminUser login(String username, String password);

    //根据用户id，获取用户信息
    AdminUser getUserDetailById(Integer loginUserId);

    //修改当前登录用户的名称信息
    boolean updateName(Integer loginUserId,String loginUserName,String nickName);

    //修改当前登录用户的密码
    boolean updatePassword(Integer loginUserId,String originalPassword,String newPassword);
}


