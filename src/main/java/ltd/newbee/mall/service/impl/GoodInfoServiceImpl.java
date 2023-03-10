package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallSearchGoodsVO;
import ltd.newbee.mall.dao.GoodInfoMapper;
import ltd.newbee.mall.entity.GoodInfo;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.service.GoodInfoService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GoodInfoServiceImpl implements GoodInfoService {
    @Autowired
    private GoodInfoMapper goodInfoMapper;

    //后台商品管理，列表
    @Override
    public PageResult getGoodInfoPage(PageQueryUtil pageQueryUtil) {
        List<GoodInfo> goodInfoList = goodInfoMapper.findGoodInfoList(pageQueryUtil);
        int totalGoodInfo = goodInfoMapper.getTotalGoodInfo(pageQueryUtil);
        PageResult pageResult = new PageResult(goodInfoList, totalGoodInfo, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    // 后台商品管理：添加商品
    @Override
    public String saveNewBeeMallGoods(GoodInfo goodInfo) {
        if(goodInfoMapper.insertSelective(goodInfo) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    // 后台商品管理，修改商品信息
    @Override
    public String updateNewBeeMallGoods(GoodInfo goods) {
        // 后台商品管理，根据 id获取商品对象
        GoodInfo temp = goodInfoMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        // 后台商品管理，修改商品信息
        if (goodInfoMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    // id获取商品详情
    @Override
    public GoodInfo getNewBeeMallGoodsById(Long id) {
        return goodInfoMapper.selectByPrimaryKey(id);
    }

    // 后台商品管理，批量修改销售状态(上架下架)
    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodInfoMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }


    // 前台模糊商品搜索
    @Override
    public PageResult searchNewBeeMallGoods(PageQueryUtil pageQueryUtil) {
        // 前台模糊搜索商品列表
        List<GoodInfo> goodsList = goodInfoMapper.findNewBeeMallGoodsListBySearch(pageQueryUtil);
        // 前台模糊商品总记录数
        int total = goodInfoMapper.getTotalNewBeeMallGoodsBySearch(pageQueryUtil);
        List<NewBeeMallSearchGoodsVO> newBeeMallSearchGoodsVOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(goodsList)){
            newBeeMallSearchGoodsVOS = BeanUtil.copyList(goodsList, NewBeeMallSearchGoodsVO.class);
            for (NewBeeMallSearchGoodsVO newBeeMallSearchGoodsVO:newBeeMallSearchGoodsVOS) {
                String goodsName = newBeeMallSearchGoodsVO.getGoodsName();
                String goodsIntro = newBeeMallSearchGoodsVO.getGoodsIntro();
                //字符串过长导致文字超出的问题
                if(goodsName.length() > 28){
                    goodsName = goodsName.substring(0, 28) + "...";
                    newBeeMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if(goodsIntro.length() > 30){
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    newBeeMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(newBeeMallSearchGoodsVOS, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }
}
