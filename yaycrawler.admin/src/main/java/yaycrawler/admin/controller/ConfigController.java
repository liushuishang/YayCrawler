package yaycrawler.admin.controller;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import yaycrawler.common.domain.FieldParseRule;
import yaycrawler.common.domain.PageParseRegion;
import yaycrawler.common.domain.UrlParseRule;
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
//@RequestMapping("/config")
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
    public List<PageParseRegion> getPageRegionsByUrl(String url) {

        List<PageParseRegion> pageRegions = pageParseRuleService.getPageRegionList(url);
        return pageRegions;
    }
    @RequestMapping("/queryPageRulesByUrl")
    @ResponseBody
    public Object queryPageRulesByUrl(String url)
    {        url = "http://floor.0731fdc.com/jggs.php";
        Assert.notNull(url);
        List<PageParseRegion> pageRegions = getPageRegionsByUrl(url);
        List<Map<String, Object>> allRuleList = new LinkedList<>();

        for (PageParseRegion pageRegion : pageRegions) {
            List<Map<String,Object>> regionRuleList=new LinkedList<>();
            String regionName = pageRegion.getName();
            String cssSelector = pageRegion.getCSSSelector();
            for (FieldParseRule fieldParseRule : pageRegion.getFieldParseRules()) {
                Map fieldRule = new HashMap<>();
                fieldRule.put("regionName", regionName);
                fieldRule.put("cssSelector", cssSelector);
                fieldRule.put("ruleType", "字段");
                fieldRule.put("fieldName", fieldParseRule.getFieldName());
                fieldRule.put("rule", fieldParseRule.getRule());

                regionRuleList.add(fieldRule);
            }
            for (UrlParseRule urlParseRule : pageRegion.getUrlParseRules()) {

                Map fieldRule = new HashMap<>();
                fieldRule.put("regionName", regionName);
                fieldRule.put("cssSelector", cssSelector);
                fieldRule.put("ruleType", "子级URL");
                fieldRule.put("fieldName", "");
                fieldRule.put("rule", urlParseRule.getRule());

                regionRuleList.add(fieldRule);
            }
            allRuleList.addAll(regionRuleList);
        }
        return allRuleList;
    }


    @RequestMapping(value = "/testPageWithRule", method = RequestMethod.POST)
    @ResponseBody
    public Object testPage(HttpServletRequest request, @RequestParam(required = true) String targetUrl,
                           @RequestBody PageParseRegion region) {
        HttpSession session = request.getSession(true);
        Page page = (Page) session.getAttribute("page");
        Map<String, Object> testResult = configSpiderService.test(targetUrl, region, page, null);

        Map<String, Object> data = MapUtils.getMap(testResult, "data");
        page = (Page) testResult.get("page");
        if (page == null) return null;

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

    @RequestMapping(value = "/saveFieldRule", method = RequestMethod.POST)
    @ResponseBody
    public Object saveFieldRule(@RequestBody Map<String, Object> params) {
        try {
            Map fieldParseRuleMap = MapUtils.getMap(params, "fieldParseRule");
            if (fieldParseRuleMap == null) return false;

            PageParseRegion region = getPageParseRegion(params);
            if (region == null) return false;

            String fieldName = MapUtils.getString(fieldParseRuleMap, "fieldName");
            String rule = MapUtils.getString(fieldParseRuleMap, "rule");
            FieldParseRule fieldParseRule = new FieldParseRule(fieldName, rule);
            fieldParseRule.setRegionId(region.getId());

            pageParseRuleService.saveFieldParseRule(fieldParseRule);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }


    @RequestMapping(value = "/saveUrlRule", method = RequestMethod.POST)
    @ResponseBody
    public Object saveUrlRule(@RequestBody Map<String, Object> params) {
        try {
            Map urlParseRuleMap = MapUtils.getMap(params, "urlParseRule");
            if (urlParseRuleMap == null) return false;

            PageParseRegion region = getPageParseRegion(params);
            if (region == null) return false;

            String rule = MapUtils.getString(urlParseRuleMap, "rule");
            UrlParseRule urlParseRule = new UrlParseRule(rule);
            urlParseRule.setRegionId(region.getId());

            pageParseRuleService.saveUrlParseRule(urlParseRule);

        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private PageParseRegion getPageParseRegion(@RequestBody Map<String, Object> params) {
        String pageUrl = MapUtils.getString(params, "pageUrl");
        String regionName = MapUtils.getString(params, "name");
        String cssSelector = MapUtils.getString(params, "cssselector");
        PageParseRegion region = pageParseRuleService.findRegionByUrlAndName(pageUrl, regionName);
        if (region == null) {
            PageParseRegion newRegion = new PageParseRegion(regionName, pageUrl, cssSelector);
            region = pageParseRuleService.savePageRegion(newRegion);
        }
        return region;
    }

}
