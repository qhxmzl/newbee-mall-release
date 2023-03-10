package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.IndexConfigTypeEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.entity.IndexConfig;
import ltd.newbee.mall.service.CarouselService;
import ltd.newbee.mall.service.IndexConfigService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

//后台商品种类配置（热销商品、新品上线、为你推荐）
@Controller
@RequestMapping("/admin")
public class IndexConfigController {
    @Autowired
    private IndexConfigService indexConfigService;

    //后台商品配置页面显示
    @GetMapping("/indexConfigs")
    public String login(HttpServletRequest request,@RequestParam("configType") int configType) {
        //从前端地址参数获取该页面为哪种类型的商品
        IndexConfigTypeEnum indexConfigTypeEnum = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);
        if (indexConfigTypeEnum.equals(IndexConfigTypeEnum.DEFAULT)) {
            return "error/error_5xx";
        }
        request.setAttribute("path", indexConfigTypeEnum.getName());
        request.setAttribute("configType", configType);
        return "admin/newbee_mall_index_config";
    }

    // 后台商品配置：列表
    @GetMapping("indexConfigs/list")
    @ResponseBody
    public Result List(@RequestParam Map<String,Object> params){
        if(StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.genFailResult("参数异常");
        }
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        //获取后台商品配置：列表
        return ResultGenerator.genSuccessResult(indexConfigService.getIndexConfigPage(pageQueryUtil));
    }

    //后台商品配置：新增
    @PostMapping("/indexConfigs/save")
    @ResponseBody
    public Result save(@RequestBody IndexConfig indexConfig){
        if(Objects.isNull(indexConfig.getConfigType()) || StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigRank())){
            return ResultGenerator.genFailResult("参数异常");
        }
        // 后台商品配置：新增
        String result = indexConfigService.insertSelective(indexConfig);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    //后台商品配置：修改
    @PostMapping("/indexConfigs/update")
    @ResponseBody
    public Result update(@RequestBody IndexConfig indexConfig){
        if(Objects.isNull(indexConfig.getConfigId())
            || Objects.isNull(indexConfig.getConfigType())
            || StringUtils.isEmpty(indexConfig.getConfigName())
            || StringUtils.isEmpty(indexConfig.getRedirectUrl())
            || Objects.isNull(indexConfig.getConfigRank())){
            return ResultGenerator.genFailResult("参数异常");
        }
        // 后台商品配置：修改
        String result = indexConfigService.updateConfigPage(indexConfig);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    //后台商品配置：详情
    @GetMapping("/indexConfigs/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        // 后台商品配置：详情
        IndexConfig config = indexConfigService.getIndexConfigById(id);
        if (config == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(config);
    }

    //后台商品配置：删除
    @PostMapping("/indexConfigs/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids){
        if(ids.length < 1){
            return ResultGenerator.genFailResult("参数异常");
        }
        // 后台商品配置：批量删除
        if(indexConfigService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("删除失败");
        }

    }
}
