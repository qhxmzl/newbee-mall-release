package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.IndexConfigTypeEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallIndexCarouselVO;
import ltd.newbee.mall.controller.vo.NewBeeMallIndexCategoryVO;
import ltd.newbee.mall.controller.vo.NewBeeMallIndexConfigGoodsVO;
import ltd.newbee.mall.service.CarouselService;
import ltd.newbee.mall.service.GoodsCategoryService;
import ltd.newbee.mall.service.IndexConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

//首页
@Controller
public class IndexController {
    @Autowired
    private IndexConfigService indexConfigService;

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private GoodsCategoryService goodsCategoryService;

    @GetMapping({"/index","/","/index.html"})
    public String indexPage(HttpServletRequest request){
        //分类数据
        List<NewBeeMallIndexCategoryVO> categories = goodsCategoryService.getCategoriesForIndex();
        if(CollectionUtils.isEmpty(categories)){
            return "error/error_5xx";
        }
        //轮播图
        List<NewBeeMallIndexCarouselVO> carousels = carouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        //热销商品
        List<NewBeeMallIndexConfigGoodsVO> hotGoodses = indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        //新品
        List<NewBeeMallIndexConfigGoodsVO> newGoodses = indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        //推荐商品
        List<NewBeeMallIndexConfigGoodsVO> recommendGoodses = indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("categories", categories);//分类数据
        request.setAttribute("carousels", carousels);//轮播图
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品
        return "mall/index";
    }
}
