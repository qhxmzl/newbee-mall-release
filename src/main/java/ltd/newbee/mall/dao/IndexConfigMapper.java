package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IndexConfigMapper {
    // 获取后台商品配置：列表
    List<IndexConfig> findIndexConfigList(PageQueryUtil pageQueryUtil);

    //获取后台商品配置：查询总记录数
    int getTotalIndexConfig(PageQueryUtil pageQueryUtil);

    //后台商品配置：新增
    int insertSelective(IndexConfig indexConfig);

    //根据id获取此商品配置详情
    IndexConfig selectByPrimaryKey(Long indexConfig);
    // 后台商品配置：修改
    int updateByPrimaryKeySelective(IndexConfig indexConfig);

    //后台商品配置：批量删除
    int deleteBatch(Integer[] ids);


    //通过类型查询商品
    List<IndexConfig>findIndexConfigsByTypeAndNum(@Param("configType") int configType, @Param("number") int number);
}
