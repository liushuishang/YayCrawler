package yaycrawler.admin.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.utils.UrlUtils;
import yaycrawler.dao.domain.*;
import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.spider.service.ConfigSpiderService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by yuananyun on 2016/5/3.
 */
@Controller
public class ConfigController {
    private static Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    private PageParserRuleService pageParseRuleService;


    @Autowired
    private ConfigSpiderService configSpiderService;

    @RequestMapping("/pageRuleManagement")
    public ModelAndView pageRuleManagement() {
        return new ModelAndView("rule_management");
    }


    @RequestMapping("/pageExpressionTest")
    public ModelAndView pageExpressionTest(String pageId) {
        ModelAndView mv = new ModelAndView("page_expression_test");
        mv.addObject("pageId", pageId);
        return mv;
    }

    @RequestMapping(value = "/testExpressionOnPage", method = RequestMethod.POST)
    @ResponseBody
    public Object testExpressionOnPage(String pageId, String expression) {
        Assert.notNull(pageId);
        Assert.notNull(expression);

        PageInfo pageInfo = pageParseRuleService.getPageInfoById(pageId);
        return configSpiderService.testExpressionOnPage(pageInfo, expression);
    }


    @RequestMapping(value = "/queryPageInfos", method = RequestMethod.GET)
    @ResponseBody
    public Object queryPageInfos(int pageIndex, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        Page<PageInfo> data = pageParseRuleService.queryPageInfos(pageIndex, pageSize);
        result.put("rows", data.getContent());
        result.put("total", data.getTotalElements());
        return result;
    }

    @RequestMapping(value = "/addPageInfo", method = RequestMethod.GET)
    public ModelAndView addPageInfo() {
        return new ModelAndView("add_page_info");
    }

    @RequestMapping(value = "/savePageInfo", method = RequestMethod.POST)
    @ResponseBody
    public Object savePageInfo(@RequestBody PageInfo pageInfo) {
        Assert.notNull(pageInfo.getPageUrl());
        Assert.notNull(pageInfo.getUrlRgx());
        String paramsJson = pageInfo.getParamsJson();
        if (!StringUtils.isBlank(paramsJson)) {
            try {
//                paramsJson = paramsJson.replace("\"", "\\\"");
                JSON.parseObject(paramsJson);
                pageInfo.setParamsJson(paramsJson);
            } catch (Exception ex) {
                return RestFulResult.failure("请求参数不是一个合法的Json字符串！");
            }
        }
        return pageParseRuleService.savePageInfo(pageInfo);
    }

    @RequestMapping(value = "/testPageValidation", method = RequestMethod.POST)
    @ResponseBody
    public Object ValidationPageInfo(@RequestBody PageInfo pageInfo) {
        return configSpiderService.testExpressionOnPage(pageInfo, pageInfo.getPageValidationRule());
    }

    @RequestMapping(value = "/deletePageInfoByIds", method = RequestMethod.POST)
    @ResponseBody
    public Object deletePageInfoByIds(@RequestBody List<String> deleteIds) {
        return pageParseRuleService.deletePageInfoByIds(deleteIds);
    }


    @RequestMapping(value = "/addPageRegion", method = RequestMethod.GET)
    public ModelAndView addPageRegion(String pageId) {
        PageInfo pageInfo = pageParseRuleService.getPageInfoById(pageId);
        ModelAndView mv = new ModelAndView("add_page_region");
        mv.addObject("pageInfo", pageInfo);
        return mv;
    }

    @RequestMapping(value = "/savePageRegion", method = RequestMethod.POST)
    @ResponseBody
    public Object savePageRegion(@RequestBody PageParseRegion region) {
        return pageParseRuleService.savePageRegion(region);
    }

    @RequestMapping(value = "/deletePageRegionByIds", method = RequestMethod.POST)
    @ResponseBody
    public Object deletePageRegionByIds(@RequestBody List<String> deleteIds) {
        return pageParseRuleService.deletePageRegionByIds(deleteIds);
    }


    @RequestMapping(value = "/addFieldRule", method = RequestMethod.GET)
    public ModelAndView addFieldRule(String regionId) {
        PageParseRegion regionInfo = pageParseRuleService.getPageRegionById(regionId);
        ModelAndView mv = new ModelAndView("add_field_rule");
        mv.addObject("regionInfo", regionInfo);
        return mv;
    }

    @RequestMapping(value = "/saveFieldRule", method = RequestMethod.POST)
    @ResponseBody
    public Object saveFieldRule(@RequestBody FieldParseRule fieldRule) {
        return pageParseRuleService.saveFieldParseRule(fieldRule);
    }

    @RequestMapping(value = "/deleteFieldRuleByIds", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteFieldRuleByIds(@RequestBody List<String> deleteIds) {
        return pageParseRuleService.deleteFieldRuleByIds(deleteIds);
    }


    @RequestMapping(value = "/addUrlRule", method = RequestMethod.GET)
    public ModelAndView addUrlRule(String regionId) {
        PageParseRegion regionInfo = pageParseRuleService.getPageRegionById(regionId);
        ModelAndView mv = new ModelAndView("add_url_rule");
        mv.addObject("regionInfo", regionInfo);
        return mv;
    }

    @RequestMapping(value = "/saveUrlRule", method = RequestMethod.POST)
    @ResponseBody
    public Object saveUrlRule(@RequestBody UrlParseRule urlRule) {
        return pageParseRuleService.saveUrlParseRule(urlRule);
    }

    @RequestMapping(value = "/deleteUrlRuleByIds", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteUrlRuleByIds(@RequestBody List<String> deleteIds) {
        return pageParseRuleService.deleteUrlRuleByIds(deleteIds);
    }

    @RequestMapping(value = "/addUrlRuleParam", method = RequestMethod.GET)
    public ModelAndView addUrlRuleParam(String urlRuleId) {
        UrlParseRule urlParseRule = pageParseRuleService.getUrlParseRuleById(urlRuleId);
        ModelAndView mv = new ModelAndView("add_url_param");
        mv.addObject("urlParseRule", urlParseRule);
        return mv;
    }

    @RequestMapping(value = "/saveUrlRuleParam", method = RequestMethod.POST)
    @ResponseBody
    public Object saveUrlRuleParam(@RequestBody UrlRuleParam urlRuleParam) {
        return pageParseRuleService.saveUrlRuleParam(urlRuleParam);
    }

    @RequestMapping(value = "/deleteUrlRuleParamByIds", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteUrlRuleParamByIds(@RequestBody List<String> deleteIds) {
        return pageParseRuleService.deleteUrlRuleParamByIds(deleteIds);
    }

    @RequestMapping(value = "/testPageRegionRule")
    @ResponseBody
    public Object testPageRegion(@RequestBody PageParseRegion parseRegion) {
        PageInfo pageInfo = pageParseRuleService.getPageInfoById(parseRegion.getPageId());
        String targetUrl = pageInfo.getPageUrl();
        Map<String, Object> paramsMap = pageInfo.getParamsMap();
        CrawlerRequest request = new CrawlerRequest(targetUrl, UrlUtils.getDomain(targetUrl), pageInfo.getMethod());
        request.setData(paramsMap);

        return configSpiderService.testRegionRule(request, parseRegion, null, null);
    }

    @RequestMapping(value = "/testPageWithRule")
    @ResponseBody
    public Object testPage(@RequestBody Map ruleMap) {
        String regionId = MapUtils.getString(ruleMap, "regionId");
        String rule = MapUtils.getString(ruleMap, "rule");
//        String scope = MapUtils.getString(ruleMap, "scope", "single");

        Assert.notNull(regionId);
        Assert.notNull(rule);

        PageParseRegion regionInDb = pageParseRuleService.getPageRegionById(regionId);
        PageParseRegion region = new PageParseRegion();
        region.setSelectExpression(regionInDb.getSelectExpression());

        String ruleType = MapUtils.getString(ruleMap, "ruleType");
        String ruleId = MapUtils.getString(ruleMap, "id", null);
        if ("fieldRule".equals(ruleType)) {
            FieldParseRule fieldParseRule = new FieldParseRule(MapUtils.getString(ruleMap, "fieldName"), rule);
            region.getFieldParseRules().add(fieldParseRule);
        } else {
            UrlParseRule urlParseRule = null;
//            ruleId不为空则是从数据库中拿取规则测试
            if (StringUtils.isBlank(ruleId)) {
                urlParseRule = new UrlParseRule(rule);
                urlParseRule.setMethod(MapUtils.getString(ruleMap, "method"));
            } else {
                for (UrlParseRule uRule : regionInDb.getUrlParseRules()) {
                    if (uRule.getId().equals(ruleId)) {
                        urlParseRule = uRule;
                        break;
                    }
                }
            }
            region.getUrlParseRules().add(urlParseRule);
        }
        PageInfo pageInfo = pageParseRuleService.getPageInfoById(regionInDb.getPageId());
        String targetUrl = pageInfo.getPageUrl();
        Map<String, Object> paramsMap = pageInfo.getParamsMap();
        CrawlerRequest request = new CrawlerRequest(targetUrl, UrlUtils.getDomain(targetUrl), pageInfo.getMethod());
        request.setData(paramsMap);

        return configSpiderService.testRegionRule(request, region, null, null);
    }

    @RequestMapping(value = "/testUrlRuleParam")
    @ResponseBody
    public Object testUrlRuleParam(@RequestBody Map ruleParamMap) {
        String urlRuleId = MapUtils.getString(ruleParamMap, "urlRuleId");
        String paramName = MapUtils.getString(ruleParamMap, "paramName");
        String paramExpression = MapUtils.getString(ruleParamMap, "expression");

        Assert.notNull(urlRuleId);
        Assert.notNull(paramName);
        Assert.notNull(paramExpression);

        UrlParseRule urlParseRule = pageParseRuleService.getUrlParseRuleById(urlRuleId);
        String regionId = urlParseRule.getRegionId();
        PageInfo pageInfo = pageParseRuleService.getPageInfoById(pageParseRuleService.getPageRegionById(regionId).getPageId());


        PageParseRegion testRegion = new PageParseRegion();
        UrlParseRule testUrlParseRule = new UrlParseRule(urlParseRule.getRule());
        testUrlParseRule.setUrlRuleParams(new HashSet<UrlRuleParam>());
        testUrlParseRule.getUrlRuleParams().add(new UrlRuleParam(null, paramName, paramExpression));
        testRegion.getUrlParseRules().add(testUrlParseRule);


        String targetUrl = pageInfo.getPageUrl();
        Map<String, Object> paramsMap = pageInfo.getParamsMap();
        CrawlerRequest request = new CrawlerRequest(targetUrl, UrlUtils.getDomain(targetUrl), pageInfo.getMethod());
        request.setData(paramsMap);

        return configSpiderService.testRegionRule(request, testRegion, null, null);
    }


    @RequestMapping("/siteManagement")
    public ModelAndView siteManagement() {
        return new ModelAndView("site_management");
    }

    @RequestMapping("/accountManagement")
    public ModelAndView accountManagement() {
        return new ModelAndView("account_management");
    }

    @RequestMapping(value = "/queryPageSites", method = RequestMethod.GET)
    @ResponseBody
    public Object querySites(int pageIndex, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        Page<PageSite> data = pageParseRuleService.querySites(pageIndex, pageSize);
        result.put("rows", data.getContent());
        result.put("total", data.getTotalElements());

        return result;
    }

    @RequestMapping(value = "/queryPageAccounts", method = RequestMethod.GET)
    @ResponseBody
    public Object queryAccounts(int pageIndex, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        Page<SiteAccount> data = pageParseRuleService.queryAccounts(pageIndex, pageSize);
        result.put("rows", data.getContent());
        result.put("total", data.getTotalElements());
        return result;
    }

    @RequestMapping(value = "/addSite", method = RequestMethod.POST)
    @ResponseBody
    public Object addSite(PageSite pageSite) {
        if (StringUtils.isBlank(pageSite.getDomain())) {
            return false;
        }
        return pageParseRuleService.addSite(pageSite);
    }

    @RequestMapping(value = "/deleteSites", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteSites(@RequestBody List<String> deleteIds) {
        Assert.notNull(deleteIds);
        return pageParseRuleService.deleteSiteByIds(deleteIds);
    }

    @RequestMapping(value = "/addAccount", method = RequestMethod.POST)
    @ResponseBody
    public Object addAccount(SiteAccount siteAccount) {
        if (StringUtils.isBlank(siteAccount.getDomain())) {
            return false;
        }
        return pageParseRuleService.addAccount(siteAccount);
    }

    @RequestMapping(value = "/deleteAccounts", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteAccounts(@RequestBody List<String> deleteIds) {
        Assert.notNull(deleteIds);
        return pageParseRuleService.deleteAccountByIds(deleteIds);
    }

    @RequestMapping(value = "/testPageUrlMatchRegex", method = RequestMethod.POST)
    @ResponseBody
    public Object testPageUrlMatchRegex(@RequestBody String pageUrl) {
        Assert.notNull(pageUrl);
        PageInfo result = pageParseRuleService.findOnePageInfoByRgx(pageUrl);
        return result;
    }


}
