package yaycrawler.common.service;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yaycrawler.common.dao.repositories.FieldParseRuleRepository;
import yaycrawler.common.dao.repositories.PageRegionRepository;
import yaycrawler.common.dao.repositories.UrlParseRuleRepository;
import yaycrawler.common.domain.FieldParseRule;
import yaycrawler.common.domain.PageParseRegion;
import yaycrawler.common.domain.UrlParseRule;
import yaycrawler.common.utils.UrlUtils;

import java.util.List;
import java.util.Map;

/**
 * 用于管理页面字段和Url抽取规则
 * Created by yuananyun on 2016/5/1.
 */
@Service
public class PageParserRuleService {

    @Autowired
    private PageRegionRepository regionRepository;

    @Autowired
    private FieldParseRuleRepository fieldParseRuleRepository;

    @Autowired
    private UrlParseRuleRepository urlParseRuleRepository;


    private Map<String, List<PageParseRegion>> urlPageParseRuleMap = new HashedMap();

    public List<PageParseRegion> getPageRegionList(String pageUrl) {
        if (StringUtils.isBlank(pageUrl)) return null;
        String url = UrlUtils.getContextPath(pageUrl);

        List<PageParseRegion> regionList = urlPageParseRuleMap.get(url);
        if (regionList == null) {
            regionList = regionRepository.findByPageUrl(url);
            if (regionList != null)
                urlPageParseRuleMap.put(url, regionList);
        }
        return regionList;
    }

    public PageParseRegion findRegionByUrlAndName(String pageUrl,String name)
    {
        List<PageParseRegion> regionList = regionRepository.findByPageUrlAndName(pageUrl, name);
        if(regionList!=null&&regionList.size()>0) return regionList.get(0);
        return null;
    }

    public boolean saveFieldParseRule(FieldParseRule fieldParseRule) {
        return fieldParseRuleRepository.save(fieldParseRule) != null;
    }

    public boolean saveUrlParseRule(UrlParseRule urlParseRule) {
        return urlParseRuleRepository.save(urlParseRule) != null;
    }


    public PageParseRegion savePageRegion(PageParseRegion newRegion) {
        return regionRepository.save(newRegion);
    }
}
