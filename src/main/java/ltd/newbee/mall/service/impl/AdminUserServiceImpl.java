package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.AdminUserMapper;
import ltd.newbee.mall.entity.AdminUser;
import ltd.newbee.mall.service.AdminUserService;
import ltd.newbee.mall.util.MD5Util;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    @Autowired
    private AdminUserMapper adminUserMapper;

    @Override
    public AdminUser login(String username, String password) {
        String md5Password = MD5Util.MD5Encode(password,"utf-8");
        // System.out.println(password);
        return adminUserMapper.login(username, md5Password);
    }

    @Override
    public AdminUser getUserDetailById(Integer loginUserId) {
        return adminUserMapper.selectByPrimarykey( loginUserId);
    }

    //修改用户名
    @Override
    public boolean updateName(Integer loginUserId, String loginUserName, String nickName) {
        AdminUser adminUser = adminUserMapper.selectByPrimarykey(loginUserId);
        //当用户非空时才可以进行更改
        if(adminUser != null){
            //设置新名称并修改
            adminUser.setLoginUserName(loginUserName);
            adminUser.setNickName(nickName);
            if(adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0){
                return true;
            }
        }
        return false;
    }

    //修改密码
    @Override
    public boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectByPrimarykey(loginUserId);
        //当前用户非空才可进行更改
        if(adminUser != null){
            String originalPasswordMd5 = MD5Util.MD5Encode(originalPassword, "utf-8");
            String newPasswordMd5 = MD5Util.MD5Encode(newPassword, "utf-8");
            //比较原密码是否正确
            if(originalPasswordMd5.equals(adminUser.getLoginPassword())){
                //设置新密码并修改
                adminUser.setLoginPassword(newPasswordMd5);
                if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0) {
                    //修改成功则返回true
                    return true;
                }
            }
        }
        return false;
    }
}

