package com.nb6868.onex.modules.shop.controller.app;

import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.util.ParamUtils;
import com.nb6868.onex.common.annotation.DataFilter;
import com.nb6868.onex.modules.shop.dto.GoodsCategoryDTO;
import com.nb6868.onex.modules.shop.dto.GoodsCategoryTreeDTO;
import com.nb6868.onex.modules.shop.service.GoodsCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * APP商品类别
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("app/shop/goodsCategory")
@Validated
@Api(tags="商品类别")
public class AppGoodsCategoryController {

    @Autowired
    private GoodsCategoryService categoryService;

    @DataFilter(tableAlias = "shop_goods_category", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<GoodsCategoryDTO> list = categoryService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "shop_goods_category", tenantFilter = true)
    @GetMapping("tree")
    @ApiOperation("树表")
    public Result<?> tree(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<GoodsCategoryTreeDTO> tree = categoryService.tree(params);

        // 使用迭代器的删除方法删除
        if (ParamUtils.toBoolean(params.get("filterEmptyChild"), false)) {
            tree.removeIf(categoryTree -> ObjectUtils.isEmpty(categoryTree.getChildren()));
        }

        return new Result<>().success(tree);
    }
}
