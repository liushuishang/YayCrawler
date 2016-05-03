package yaycrawler.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import yaycrawler.common.domain.PageParseRegion;
import yaycrawler.common.service.PageParserRuleService;

import java.util.List;

/**
 * Created by yuananyun on 2016/5/3.
 */
@Controller
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private PageParserRuleService pageParseRuleService;

    @RequestMapping("/pageRuleManagement")
    public ModelAndView pageRuleManagement() {
        return new ModelAndView("rule_management");
    }

    @RequestMapping("/getPageRegionRules")
    @ResponseBody
    public Object getPageRegionsByUrl(String url) {
        List<PageParseRegion> pageRegions = pageParseRuleService.getPageRegionParseRule(url);
        return pageRegions;
    }


}
