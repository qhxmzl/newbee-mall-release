package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.AdminUser;
import org.apache.ibatis.annotations.Param;

public interface AdminUserMapper {

    //登录方法
    AdminUser login(@Param("userName") String username, @Param("password") String password);

    //根据用户id 获取用户信息
    AdminUser selectByPrimarykey(Integer adminUserId);


    /**
     * 修改用户信息
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(AdminUser record);

    /**
     * 修改用户密码
     * @param record
     * @return
     */
    int updateByPrimaryKey(AdminUser record);
}
