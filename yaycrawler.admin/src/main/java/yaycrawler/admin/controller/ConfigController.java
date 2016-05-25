package yaycrawler.admin.controller;

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
import yaycrawler.common.utils.UrlUtils;
import yaycrawler.dao.domain.*;
import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.spider.service.ConfigSpiderService;

import java.util.HashMap;
import java.util.LinkedList;
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


    @RequestMapping(value = "/queryPageInfos", method = RequestMethod.GET)
    @ResponseBody
    public Object queryPageInfos(int pageIndex,int pageSize)
    {
        Map<String,Object> result=new HashMap<>();
        Page<PageInfo> data = pageParseRuleService.queryPageInfos(pageIndex, pageSize);
        result.put("rows", data.getContent());
        result.put("total", data.getTotalElements());
        return result;
    }
    @RequestMapping(value = "/addPageInfo",method = RequestMethod.GET)
    public ModelAndView addPageInfo() {
        return new ModelAndView("add_page_info");
    }
    @RequestMapping(value = "/savePageInfo",method = RequestMethod.POST)
    @ResponseBody
    public Object savePageInfo(@RequestBody PageInfo pageInfo) {
        Assert.notNull(pageInfo.getPageUrl());
        return pageParseRuleService.savePageInfo(pageInfo);
    }
    @RequestMapping(value = "/deletePageInfoByIds",method = RequestMethod.POST)
    @ResponseBody
    public Object deletePageInfoByIds(@RequestBody  List<String> deleteIds) {
        return pageParseRuleService.deletePageInfoByIds(deleteIds);
    }



    @RequestMapping(value = "/addPageRegion",method = RequestMethod.GET)
    public ModelAndView addPageRegion(String pageId) {
        PageInfo pageInfo = pageParseRuleService.getPageInfoById(pageId);
        ModelAndView mv = new ModelAndView("add_page_region");
        mv.addObject("pageInfo", pageInfo);
        return mv;
    }
    @RequestMapping(value = "/savePageRegion",method = RequestMethod.POST)
    @ResponseBody
    public Object savePageRegion( @RequestBody PageParseRegion region) {
        return pageParseRuleService.savePageRegion(region);
    }
    @RequestMapping(value = "/deletePageRegionByIds",method = RequestMethod.POST)
    @ResponseBody
    public Object deletePageRegionByIds(@RequestBody List<String> deleteIds) {
        return pageParseRuleService.deletePageRegionByIds(deleteIds);
    }




    @RequestMapping(value = "/addFieldRule",method = RequestMethod.GET)
    public ModelAndView addFieldRule(String regionId) {
        PageParseRegion regionInfo = pageParseRuleService.getPageRegionById(regionId);
        ModelAndView mv = new ModelAndView("add_field_rule");
        mv.addObject("regionInfo", regionInfo);
        return mv;
    }
    @RequestMapping(value = "/saveFieldRule",method = RequestMethod.POST)
    @ResponseBody
    public Object saveFieldRule(@RequestBody FieldParseRule fieldRule) {
        return pageParseRuleService.saveFieldParseRule(fieldRule);
    }
    @RequestMapping(value = "/deleteFieldRuleByIds",method = RequestMethod.POST)
    @ResponseBody
    public Object deleteFieldRuleByIds(@RequestBody List<String> deleteIds) {
        return pageParseRuleService.deleteFieldRuleByIds(deleteIds);
    }


    @RequestMapping(value = "/addUrlRule",method = RequestMethod.GET)
    public ModelAndView addUrlRule(String regionId) {
        PageParseRegion regionInfo = pageParseRuleService.getPageRegionById(regionId);
        ModelAndView mv = new ModelAndView("add_url_rule");
        mv.addObject("regionInfo", regionInfo);
        return mv;
    }
    @RequestMapping(value = "/saveUrlRule",method = RequestMethod.POST)
    @ResponseBody
    public Object saveUrlRule(@RequestBody UrlParseRule urlRule) {
        return pageParseRuleService.saveUrlParseRule(urlRule);
    }
    @RequestMapping(value = "/deleteUrlRuleByIds",method = RequestMethod.POST)
    @ResponseBody
    public Object deleteUrlRuleByIds(@RequestBody List<String> deleteIds) {
        return pageParseRuleService.deleteUrlRuleByIds(deleteIds);
    }




    @RequestMapping(value = "/testPageWithRule")
    @ResponseBody
    public Object testPage(@RequestBody Map ruleMap) {
        String regionId=MapUtils.getString(ruleMap, "regionId");
        String rule = MapUtils.getString(ruleMap, "rule");

        Assert.notNull(regionId);
        Assert.notNull(rule);

        PageParseRegion region = pageParseRuleService.getPageRegionById(regionId);
        String ruleType = MapUtils.getString(ruleMap, "ruleType");
        if("fieldRule".equals(ruleType))
        {
            FieldParseRule fieldParseRule = new FieldParseRule(MapUtils.getString(ruleMap, "fieldName"), rule);
            List<FieldParseRule> fieldParseRuleList = new LinkedList<>();
            fieldParseRuleList.add(fieldParseRule);
            region.setFieldParseRules(fieldParseRuleList);
            region.setUrlParseRules(null);
        }else{
            UrlParseRule urlParseRule = new UrlParseRule(rule);
            List<UrlParseRule> urlParseRuleList = new LinkedList<>();
            urlParseRuleList.add(urlParseRule);
            region.setUrlParseRules(urlParseRuleList);
            region.setFieldParseRules(null);
        }

        PageInfo pageInfo = pageParseRuleService.getPageInfoById(region.getPageId());
        String targetUrl = pageInfo.getPageUrl();
        Map<String, Object> paramsMap = pageInfo.getParamsMap();
        CrawlerRequest request=new CrawlerRequest(targetUrl, UrlUtils.getDomain(targetUrl),pageInfo.getMethod());
        request.setData(paramsMap);

        return configSpiderService.test(request, region, null, null);
    }




    @RequestMapping("/siteManagement")
    public ModelAndView siteManagement() {
        return new ModelAndView("site_management");
    }

    @RequestMapping(value = "/queryPageSites", method = RequestMethod.GET)
    @ResponseBody
    public Object querySites(int pageIndex,int pageSize)
    {
        Map<String,Object> result=new HashMap<>();
        Page<PageSite> data = pageParseRuleService.querySites(pageIndex, pageSize);
        result.put("rows", data.getContent());
        result.put("total", data.getTotalElements());

        return result;
    }

    @RequestMapping(value = "/addSite", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteSites(PageSite pageSite)
    {
        if (StringUtils.isBlank(pageSite.getDomain())) {
            return false;
        }
       return  pageParseRuleService.addSite(pageSite);
    }

    @RequestMapping(value = "/deleteSites", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteSites(@RequestBody  List<String> deleteIds)
    {
        Assert.notNull(deleteIds);
        return pageParseRuleService.deleteSiteByIds(deleteIds);
    }


}
