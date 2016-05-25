package yaycrawler.dao.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import yaycrawler.dao.domain.*;
import yaycrawler.dao.repositories.*;

import java.util.List;

/**
 * 用于管理页面字段和Url抽取规则
 * Created by yuananyun on 2016/5/1.
 */
@Service
@Transactional
public class PageParserRuleService {

//    private static Logger logger = LoggerFactory.getLogger(PageParserRuleService.class);

    @Autowired
    private PageInfoRepository pageInfoRepository;

    @Autowired
    private PageRegionRepository regionRepository;

    @Autowired
    private FieldParseRuleRepository fieldParseRuleRepository;

    @Autowired
    private UrlParseRuleRepository urlParseRuleRepository;

    @Autowired
    private PageSiteRepository siteRepository;


    public List<PageParseRegion> getPageRegionList(String pageUrl) {
        if (StringUtils.isBlank(pageUrl)) return null;

        PageInfo pageInfo = pageInfoRepository.findOneByUrlRgx(pageUrl);
        return pageInfo.getPageParseRegionList();
    }


    public boolean saveFieldParseRule(String pageUrl,String urlRegex, String pageMethod, String urlParamsJson,
                                      String pageRegionName, String regionSelectExpression,String regionDataType,
                                      String fieldName, String rule) {
        PageInfo pageInfo = createOrUpdatePageInfo(pageUrl,urlRegex, pageMethod, urlParamsJson);
        PageParseRegion region = createOrUpdateRegion(pageInfo.getId(), pageRegionName, regionSelectExpression,regionDataType);

        FieldParseRule fieldParseRule = new FieldParseRule(fieldName, rule);
        fieldParseRule.setRegionId(region.getId());

        return fieldParseRuleRepository.save(fieldParseRule) != null;
    }

    public boolean saveUrlParseRule(String pageUrl, String urlRegex,String pageMethod, String urlParamsJson,
                                    String pageRegionName, String regionSelectExpression,String regionDataType,
                                    String rule, String ruleMethod) {

        PageInfo pageInfo = createOrUpdatePageInfo(pageUrl,urlRegex, pageMethod, urlParamsJson);
        PageParseRegion region = createOrUpdateRegion(pageInfo.getId(), pageRegionName, regionSelectExpression, regionDataType);

        if(StringUtils.isBlank(ruleMethod)) ruleMethod = "GET";
        UrlParseRule urlParseRule = new UrlParseRule(rule);
        urlParseRule.setRegionId(region.getId());
        urlParseRule.setMethod(ruleMethod.toUpperCase());
        return urlParseRuleRepository.save(urlParseRule) != null;
    }

    private PageParseRegion createOrUpdateRegion(String pageId, String pageRegionName, String regionSelectExpression, String regionDataType) {
        Assert.notNull(pageId);
        Assert.notNull(regionSelectExpression);

        PageParseRegion region = regionRepository.findOneByPageIdAndSelectExpression(pageId, regionSelectExpression);
        if (region == null)
            region = new PageParseRegion();
        region.setPageId(pageId);
        region.setName(pageRegionName);
        region.setSelectExpression(regionSelectExpression);
        region.setDataType(regionDataType);

        return regionRepository.save(region);
    }


    private PageInfo createOrUpdatePageInfo(String pageUrl, String urlRegex,String pageMethod, String urlParamsJson) {
        Assert.notNull(pageUrl);
        PageInfo pageInfo = pageInfoRepository.findOneByUrlRgx(pageUrl);
        if (pageInfo == null) {
            pageInfo = new PageInfo();
        }
        pageInfo.setPageUrl(pageUrl);
        pageInfo.setUrlRgx(urlRegex);
        pageInfo.setMethod(pageMethod);
        pageInfo.setParamsJson(urlParamsJson);
        return pageInfoRepository.save(pageInfo);
    }


    public List queryAllRule() {
//        List data = regionRepository.queryAllFieldRules();
//        data.addAll(regionRepository.queryAllUrlRules());
//        return data;

        return null;
    }

    public List queryRulesByUrl(String url) {
        Assert.notNull(url);
//        List data = regionRepository.queryFieldRulesByUrl(url);
//        data.addAll(regionRepository.queryUrlRulesByUrl(url));
//        return data;
        return null;
    }

    public boolean deleteRuleByIds(String[] idArray) {
        for (int i = 0; i < idArray.length; i++) {
            String id = idArray[i];
            if (fieldParseRuleRepository.exists(id))
                fieldParseRuleRepository.delete(id);
            else
                urlParseRuleRepository.delete(id);
        }
        return true;
    }

    public Page<PageSite> querySites(int pageIndex, int pageSize) {
        return siteRepository.findAll(new PageRequest(pageIndex,pageSize));
    }

    public boolean deleteSiteByIds(List<String> deleteIds) {
        for (String deleteId : deleteIds) {
            //先删除cookies
            siteRepository.delete(deleteId);
        }
        return true;
    }

    public boolean addSite(PageSite pageSite) {
        return siteRepository.save(pageSite)!=null;
    }



    public Page<PageInfo> queryPageInfos(int pageIndex, int pageSize) {
        return pageInfoRepository.findAll(new PageRequest(pageIndex, pageSize));
    }
}
