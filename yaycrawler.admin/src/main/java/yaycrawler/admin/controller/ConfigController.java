package yaycrawler.admin.controller;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import yaycrawler.common.domain.PageParseRegion;
import yaycrawler.common.service.PageParserRuleService;
import yaycrawler.spider.service.ConfigSpiderService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuananyun on 2016/5/3.
 */
@Controller
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private PageParserRuleService pageParseRuleService;

    @Autowired
    private ConfigSpiderService configSpiderService;

    @RequestMapping("/pageRuleManagement")
    public ModelAndView pageRuleManagement() {
        return new ModelAndView("rule_management");
    }

    @RequestMapping("/getPageRegionRules")
    @ResponseBody
    public Object getPageRegionsByUrl(String url) {
        List<PageParseRegion> pageRegions = pageParseRuleService.getPageRegionList(url);
        return pageRegions;
    }

    @RequestMapping("/testPageWithRule")
    @ResponseBody
    public Object testPage(HttpServletRequest request, @RequestParam(required = true) String targetUrl,
                           @RequestBody PageParseRegion region) {
        HttpSession session = request.getSession(true);
        Page page = (Page) session.getAttribute("page");
        Map<String, Object> testResult = configSpiderService.test(targetUrl, region, page, null);

        Map<String, Object> data = MapUtils.getMap(testResult, "data");
        page = (Page) testResult.get("page");
        if(page==null) return null;

        List<String> childUrlInfoList = new LinkedList<>();
        for (Request r : page.getTargetRequests()) {
            childUrlInfoList.add(r.toString());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("childUrls", childUrlInfoList);

        Page tmpPage = new Page();
        tmpPage.setHtml(page.getHtml());
        tmpPage.setRequest(page.getRequest());
        tmpPage.setUrl(page.getUrl());
        page = null;
        session.setAttribute("page", tmpPage);
        return result;
    }

}
