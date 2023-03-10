package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallIndexCategoryVO;
import ltd.newbee.mall.controller.vo.SearchPageCategoryVO;
import ltd.newbee.mall.controller.vo.SecondLevelCategoryVO;
import ltd.newbee.mall.controller.vo.ThirdLevelCategoryVO;
import ltd.newbee.mall.dao.GoodsCategoryMapper;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.service.GoodsCategoryService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class GoodsCategoryServiceImpl implements GoodsCategoryService {
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    //分页列表
    @Override
    public PageResult getGoodsCategoryPage(PageQueryUtil pageQueryUtil) {
        List<GoodsCategory> goodsCategoryList = goodsCategoryMapper.findGoodsCategoryList(pageQueryUtil);
        int totalGoodsCategory = goodsCategoryMapper.getTotalGoodsCategory(pageQueryUtil);
        PageResult pageResult = new PageResult(goodsCategoryList, totalGoodsCategory, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    //后台"新增"商品分类
    @Override
    public String insertSelective(GoodsCategory goodsCategory) {
        //后台"新增"商品分类：根据分类等级和分类名称，查询是否已经有该分类
        GoodsCategory gc = goodsCategoryMapper.selectByCategoryLevelAndCategoryName(goodsCategory.getCategoryLevel(),
                goodsCategory.getCategoryName());
        if(gc != null){
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        goodsCategory.setUpdateTime(new Date());
        // 后台"新增"一条商品分类
        if(goodsCategoryMapper.insertSelective(goodsCategory) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //后台"修改"商品分类
    @Override
    public String updateCategoryPage(GoodsCategory goodsCategory) {
        //根据分类id查询是否有该分类，有则进行修改
        GoodsCategory goodsCategory1 = goodsCategoryMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        if(goodsCategory == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        goodsCategory1.setCategoryName(goodsCategory.getCategoryName());
        goodsCategory1.setParentId(goodsCategory.getParentId());
        goodsCategory1.setCategoryLevel(goodsCategory.getCategoryLevel());
        goodsCategory1.setCategoryRank(goodsCategory.getCategoryRank());
        goodsCategory1.setUpdateTime(new Date());
        //后台"修改"商品分类
        if(goodsCategoryMapper.updateByPrimaryKeySelective(goodsCategory1) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //根据分类id获取该分类对象
    @Override
    public GoodsCategory getGoodsCategoryById(Long id) {
        // 主键获取该分类详情
        return goodsCategoryMapper.selectByPrimaryKey(id);
    }

    //删除商品分类
    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if(ids.length < 1){
            return false;
        }
        return goodsCategoryMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<NewBeeMallIndexCategoryVO> getCategoriesForIndex() {
        List<NewBeeMallIndexCategoryVO> newBeeMallIndexCategoryVOS = new ArrayList<>();
        //获取一级分类的固定数量的数据
        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            List<Long> firstLevelCategoryIds = firstLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            //获取二级分类的数据
            List<GoodsCategory> secondLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(firstLevelCategoryIds, NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                List<Long> secondLevelCategoryIds = secondLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                //获取三级分类的数据
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(secondLevelCategoryIds, NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                    //根据 parentId 将 thirdLevelCategories 分组
                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream().collect(groupingBy(GoodsCategory::getParentId));
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    //处理二级分类
                    for (GoodsCategory secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                        //如果该二级分类下有数据则放入 secondLevelCategoryVOS 对象中
                        if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())) {
                            //根据二级分类的id取出thirdLevelCategoryMap分组中的三级分类list
                            List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.get(secondLevelCategory.getCategoryId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS((BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryVO.class)));
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }
                    //处理一级分类
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                        //根据 parentId 将 thirdLevelCategories 分组
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for (GoodsCategory firstCategory : firstLevelCategories) {
                            NewBeeMallIndexCategoryVO newBeeMallIndexCategoryVO = new NewBeeMallIndexCategoryVO();
                            BeanUtil.copyProperties(firstCategory, newBeeMallIndexCategoryVO);
                            //如果该一级分类下有数据则放入 newBeeMallIndexCategoryVOS 对象中
                            if (secondLevelCategoryVOMap.containsKey(firstCategory.getCategoryId())) {
                                //根据一级分类的id取出secondLevelCategoryVOMap分组中的二级级分类list
                                List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.get(firstCategory.getCategoryId());
                                newBeeMallIndexCategoryVO.setSecondLevelCategoryVOS(tempGoodsCategories);
                                newBeeMallIndexCategoryVOS.add(newBeeMallIndexCategoryVO);
                            }
                        }
                    }
                }
            }
            return newBeeMallIndexCategoryVOS;
        } else {
            return null;
        }
    }

    @Override
    public SearchPageCategoryVO getCategoriesForSearch(Long categoryId) {
        SearchPageCategoryVO searchPageCategoryVO = new SearchPageCategoryVO();
        GoodsCategory thirdLevelGoodsCategory = goodsCategoryMapper.selectByPrimaryKey(categoryId);
        if (thirdLevelGoodsCategory != null && thirdLevelGoodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            //获取当前三级分类的二级分类
            GoodsCategory secondLevelGoodsCategory = goodsCategoryMapper.selectByPrimaryKey(thirdLevelGoodsCategory.getParentId());
            if (secondLevelGoodsCategory != null && secondLevelGoodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
                //获取当前二级分类下的三级分类List
                // Collections是一个包装类,包含了很多静态方法,不能被实例化,就像一个工具类,比如提供的排序方法: Collections.sort(list).
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelGoodsCategory.getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel(), Constants.SEARCH_CATEGORY_NUMBER);
                searchPageCategoryVO.setCurrentCategoryName(thirdLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setSecondLevelCategoryName(secondLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setThirdLevelCategoryList(thirdLevelCategories);
                return searchPageCategoryVO;
            }
        }
        return null;
    }


    // 根据parentId和level获取分类列表
    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        return goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(parentIds, categoryLevel, 0);//0代表查询所有
    }


}
