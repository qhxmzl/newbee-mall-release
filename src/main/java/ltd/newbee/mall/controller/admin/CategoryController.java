package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.service.GoodsCategoryService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


//后台商品分类管理
@Controller
@RequestMapping("/admin")
public class CategoryController {
    @Autowired
    private GoodsCategoryService goodsCategoryService;

    //后台商品分类管理，页面显示
    @GetMapping("/categories")
    public String categoryPage(HttpServletRequest request,@RequestParam("categoryLevel") Byte categoryLevel,
                               @RequestParam("parentId") Long parentId,@RequestParam("backParentId") Long backParentId){
        if(categoryLevel == null || categoryLevel < 1 ||categoryLevel >3){
            return "error/error_5xx";
        }
        request.setAttribute("path","newbee_mall_category");
        request.setAttribute("parentId", parentId);
        request.setAttribute("backParentId", backParentId);
        request.setAttribute("categoryLevel", categoryLevel);
        return "admin/newbee_mall_category";
    }

    //后台商品分类管理，列表
    @GetMapping("/categories/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object> params){
        if(StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))
            || StringUtils.isEmpty("categoryLevel") || StringUtils.isEmpty("parentId")){
            return ResultGenerator.genFailResult("参数异常");
        }
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(goodsCategoryService.getGoodsCategoryPage(pageQueryUtil));
    }

    /**
     * 后台商品分类管理，列表,商品分级
     */
    @GetMapping("/categories/listForSelect")
    @ResponseBody
    public Result listForSelect(@RequestParam("categoryId") Long categoryId){
        if(categoryId == null || categoryId < 1){
            return ResultGenerator.genFailResult("缺少参数");
        }
        GoodsCategory category = goodsCategoryService.getGoodsCategoryById(categoryId);
        //既不是一级分类也不是二级分类则不返回数据
        if(category == null || category.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()){
            return ResultGenerator.genFailResult("参数异常");
        }
        Map categoryResult = new HashMap(2);
        if(category.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel()){
            /**
             * 如果是一级分类则返回当前一级分类下的所有二级分类，以及二级分类列表中第一条数据下的所有三级分类列表
             */
            //1.查询一级分类中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if(!CollectionUtils.isEmpty(secondLevelCategories)){
                //2.查询二级分类列表中的第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                categoryResult.put("secondLevelCategories", secondLevelCategories);
                categoryResult.put("thirdLevelCategories", thirdLevelCategories);
            }
        }
        if(category.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel()){
            //如果是二级分类则返回当前分类下的所有三级分类列表
            List<GoodsCategory> thirdLevelCategories = goodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
            categoryResult.put("thirdLevelCategories", thirdLevelCategories);
        }
        return ResultGenerator.genSuccessResult(categoryResult);
    }

    //后台商品分类管理，新增
    @PostMapping("/categories/save")
    @ResponseBody
    public Result save(@RequestBody GoodsCategory goodsCategory){
        if(Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())){
            return  ResultGenerator.genFailResult("参数异常");
        }
        //后台"新增"一条商品分类
        String result = goodsCategoryService.insertSelective(goodsCategory);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    //后台商品分类管理，修改
    @RequestMapping("/categories/update")
    @ResponseBody
    public Result update(@RequestBody GoodsCategory goodsCategory){
        if(Objects.isNull(goodsCategory.getCategoryId())
                || Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getCategoryRank())){
            return ResultGenerator.genFailResult("参数异常");
        }
        // 后台"修改"商品分类
        String result = goodsCategoryService.updateCategoryPage(goodsCategory);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 后台商品分类管理，详情
     */
    @GetMapping("/categories/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        //根据分类id获取该分类对象
        GoodsCategory goodsCategory = goodsCategoryService.getGoodsCategoryById(id);
        if (goodsCategory == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(goodsCategory);
    }

    //后台商品分类管理，删除
    @PostMapping("/categories/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids){
        if(ids.length < 1){
            return ResultGenerator.genFailResult("参数异常");
        }
        //删除商品分类
        if(goodsCategoryService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("删除失败");
        }

    }
}
