package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    List<User> findUserList(PageQueryUtil pageQueryUtil);

    //查询总记录数
    int getTotalUser(PageQueryUtil pageQueryUtil);

    //用户禁用与解除禁用(0-未锁定 1-已锁定)
    int lockUserBatch(@Param("ids") Integer[] ids, @Param("lockStatus") int lockStatus);

    //  //登录:根据用户名和密码查询用户是否存在
    User selectByLoginNameAndPasswd(@Param("loginName") String loginName,@Param("password") String password);

    //根据用户名判断此用户名是否已被注册
    User selectByLoginName(String loginName);


    //  前台注册：添加用户
    int insertSelective(User record);


    // 前台个人中心修改用户信息：根据id查询用户信息
    User selectByPrimaryKey(Long userId);

    // 前台个人中心修改用户信息
    int updateByPrimaryKeySelective(User record);
}
