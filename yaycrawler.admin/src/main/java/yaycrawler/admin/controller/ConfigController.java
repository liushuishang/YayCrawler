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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import us.codecraft.webmagic.Request;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.utils.UrlUtils;
import yaycrawler.dao.domain.PageParseRegion;
import yaycrawler.dao.domain.PageSite;
import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.spider.service.ConfigSpiderService;
import yaycrawler.spider.utils.RequestHelper;

import javax.servlet.http.HttpServletRequest;
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
    private static Logger logger = LoggerFactory.getLogger(ConfigController.class);

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
    public Object queryPageRulesByUrl(String url) {
        List<Object[]> result = null;
        if (url == null)
            result = pageParseRuleService.queryAllRule();
        else
            result = pageParseRuleService.queryRulesByUrl(url);

        List<Map<String, Object>> dataList = new LinkedList<>();
        for (Object[] valueArray : result) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", valueArray[0]);
            data.put("pageUrl", valueArray[1]);
            data.put("regionName", valueArray[2]);
            data.put("selectExpression", valueArray[3]);
            data.put("ruleType", valueArray[4]);
            data.put("fieldName", valueArray[5]);
            data.put("rule", valueArray[6]);

            dataList.add(data);
        }
        return dataList;
    }


    @RequestMapping(value = "/testPageWithRule", method = RequestMethod.POST)
    @ResponseBody
    public Object testPage(HttpServletRequest httpServletRequest, @RequestParam(required = true) String targetUrl,
                           @RequestBody PageParseRegion region) {

        String urlParamsJson = region.getUrlParamsJson();
        Map<String, Object> paramsMap = null;
        if (!StringUtils.isBlank(urlParamsJson)) {
            try {
                paramsMap = JSON.parseObject(urlParamsJson, Map.class);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                return RestFulResult.failure("Url参数不是一个合法的json");
            }
        }
        Request targetRequest = RequestHelper.createRequest(targetUrl, region.getMethod(), paramsMap);
        Map<String, Object> testResult = configSpiderService.test(targetRequest, region, null, null);
        return testResult;
    }

    @RequestMapping(value = "/saveFieldRule", method = RequestMethod.POST)
    @ResponseBody
    public Object saveFieldRule(@RequestBody Map<String, Object> params) {
        try {
            String pageUrl = MapUtils.getString(params, "pageUrl");
            pageUrl = UrlUtils.getContextPath(pageUrl);

            String pageMethod = MapUtils.getString(params, "method");
            String urlParamsJson = MapUtils.getString(params, "urlParamsJson");

            String pageRegionName = MapUtils.getString(params, "regionName");
            String regionSelectExpression = MapUtils.getString(params, "selectExpression");


            Map fieldParseRuleMap = MapUtils.getMap(params, "fieldParseRule");
            String fieldName = MapUtils.getString(fieldParseRuleMap, "fieldName");
            String rule = MapUtils.getString(fieldParseRuleMap, "rule");

            return pageParseRuleService.saveFieldParseRule(pageUrl, pageMethod, urlParamsJson, pageRegionName, regionSelectExpression, fieldName, rule);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return RestFulResult.failure(ex.getMessage());
        }
    }


    @RequestMapping(value = "/saveUrlRule", method = RequestMethod.POST)
    @ResponseBody
    public Object saveUrlRule(@RequestBody Map<String, Object> params) {
        try {
            String pageUrl = MapUtils.getString(params, "pageUrl");
            pageUrl = UrlUtils.getContextPath(pageUrl);

            String pageMethod = MapUtils.getString(params, "method");
            String urlParamsJson = MapUtils.getString(params, "urlParamsJson");

            String pageRegionName = MapUtils.getString(params, "regionName");
            String regionSelectExpression = MapUtils.getString(params, "selectExpression");

            Map urlParseRuleMap = MapUtils.getMap(params, "urlParseRule");
            String rule = MapUtils.getString(urlParseRuleMap, "rule");
            String ruleMethod=MapUtils.getString(urlParseRuleMap, "method");
            return pageParseRuleService.saveUrlParseRule(pageUrl, pageMethod, urlParamsJson, pageRegionName, regionSelectExpression, rule,ruleMethod);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return RestFulResult.failure(ex.getMessage());
        }
    }

    @RequestMapping(value = "/deleteRuleByIds", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteRuleByIds(@RequestBody String[] idArray) {
        return pageParseRuleService.deleteRuleByIds(idArray);
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
