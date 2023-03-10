package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallIndexConfigGoodsVO;
import ltd.newbee.mall.dao.GoodInfoMapper;
import ltd.newbee.mall.dao.IndexConfigMapper;
import ltd.newbee.mall.entity.GoodInfo;
import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.service.IndexConfigService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndexConfigServiceImpl implements IndexConfigService {
    @Autowired
    private IndexConfigMapper indexConfigMapper;

    @Autowired
    private GoodInfoMapper goodInfoMapper;

    // 获取后台商品配置：列表
    @Override
    public PageResult getIndexConfigPage(PageQueryUtil pageQueryUtil) {
        List<IndexConfig> indexConfigList = indexConfigMapper.findIndexConfigList(pageQueryUtil);
        int totalIndexConfig = indexConfigMapper.getTotalIndexConfig(pageQueryUtil);
        PageResult pageResult = new PageResult(indexConfigList,totalIndexConfig, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    //后台商品配置：新增
    @Override
    public String insertSelective(IndexConfig indexConfig) {
        // 后台商品配置：新增
        if(indexConfigMapper.insertSelective(indexConfig) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //后台商品配置：修改
    @Override
    public String updateConfigPage(IndexConfig indexConfig) {
        //根据id获取此商品配置详情
        IndexConfig indexConfig1 = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if(indexConfig == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        indexConfig1.setConfigName(indexConfig.getConfigName());
        indexConfig1.setRedirectUrl(indexConfig.getRedirectUrl());
        indexConfig1.setGoodsId(indexConfig.getGoodsId());
        indexConfig1.setConfigRank(indexConfig.getConfigRank());
        indexConfig1.setUpdateTime(new Date());
        // 后台商品配置：修改
        if(indexConfigMapper.updateByPrimaryKeySelective(indexConfig1) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    // 后台商品配置： 详情
    @Override
    public IndexConfig getIndexConfigById(Long id) {
        return null;
    }

    //主键获取详情
    @Override
    public IndexConfig getConfigPageById(Long id) {
        return null;
    }

    //后台商品配置：批量删除
    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if(ids.length < 1){
            return false;
        }
        // 后台商品配置：批量删除
        return indexConfigMapper.deleteBatch(ids) > 0;
    }


    // 返回固定数量的首页配置商品对象(首页调用)
    @Override
    public List<NewBeeMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<NewBeeMallIndexConfigGoodsVO> newBeeMallIndexConfigGoodsVOS = new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);
        if(!CollectionUtils.isEmpty(indexConfigs)){
            //取出所有的goodsId
            List<Long> goodsIds = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<GoodInfo> newBeeMallGoods = goodInfoMapper.selectByPrimaryKeys(goodsIds);
            newBeeMallIndexConfigGoodsVOS = BeanUtil.copyList(newBeeMallGoods, NewBeeMallIndexConfigGoodsVO.class);
            for(NewBeeMallIndexConfigGoodsVO newBeeMallIndexConfigGoodsVO : newBeeMallIndexConfigGoodsVOS){
                String goodsName = newBeeMallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = newBeeMallIndexConfigGoodsVO.getGoodsIntro();
                //字符串过长导致文字超出的问题
                if(goodsName.length() > 30){
                    goodsName = goodsName.substring(0, 30) + "...";
                    newBeeMallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22){
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    newBeeMallIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return newBeeMallIndexConfigGoodsVOS;
    }
}
