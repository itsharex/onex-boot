package com.nb6868.onex.modules.cms.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.modules.cms.dto.ArticleDTO;
import com.nb6868.onex.modules.cms.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * html页面
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Controller("CmsHtml")
@RequestMapping("cms/html")
@Api(tags = "cms html")
public class HtmlController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("文章详情页面")
    @GetMapping("article/info")
    @AccessControl
    public String articleInfo(ModelMap map, @NotNull(message = "{id.require}") @RequestParam Long id) {
        ArticleDTO data = articleService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        map.put("data", data);
        return "cms/article-info";
    }

    @GetMapping("article/page")
    @AccessControl
    public String articlePage(ModelMap map, @RequestParam Map<String, Object> params) {
        PageData<ArticleDTO> page = articleService.pageDto(params);
        map.put("page", page);
        return "cms/article-page";
    }

}
