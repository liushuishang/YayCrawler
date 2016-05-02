package yaycrawler.common.service;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yaycrawler.common.dao.repositories.PageRegionRepository;
import yaycrawler.common.domain.PageParseRegion;
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

    private Map<String, List<PageParseRegion>> urlPageParseRuleMap = new HashedMap();

    public List<PageParseRegion> getPageRegionParseRule(String pageUrl) {
        if (StringUtils.isBlank(pageUrl)) return null;
        String url = UrlUtils.getContextPath(pageUrl);

        return regionRepository.findByPageUrl(url);

//        List<PageParseRegion> pagePageParseRegionList = urlPageParseRuleMap.get(url);
//        if (pagePageParseRegionList == null) {
//            pagePageParseRegionList = new LinkedList<>();
//            if (url.contains("jggs.php")) {
//
//                /**
//                 * 列表区域
//                 */
//                PageParseRegion itemPageParseRegion = new PageParseRegion("listRegion", url, ".list-con");
//                List<UrlParseRule> itemUrlParseRuleList = new LinkedList<>();
//                itemUrlParseRuleList.add(new UrlParseRule("links().regex(.*info.php?.*).all()"));
//                itemUrlParseRuleList.add(new UrlParseRule("links().regex(.*dxx.php?.*).all()"));
//
//                List<FieldParseRule> itemFieldParseRuleList = new LinkedList<>();
//                itemFieldParseRuleList.add(new FieldParseRule("floorName", "xpath(li[@class='floorname']/a/text()).get()"));
//                itemFieldParseRuleList.add(new FieldParseRule("address", "xpath(dl/dd[1]/text()).get()"));
//
//
//                itemPageParseRegion.setFieldParseRules(itemFieldParseRuleList);
//                itemPageParseRegion.setUrlParseRules(itemUrlParseRuleList);
//
//                pagePageParseRegionList.add(itemPageParseRegion);
//
//                /**
//                 * 分页区域
//                 */
//                PageParseRegion pagedPageParseRegion = new PageParseRegion("pagedRegion", url, ".pageno");
//                List<UrlParseRule> pagedUrlParseRuleList = new LinkedList<>();
//                pagedUrlParseRuleList.add(new UrlParseRule("links().all()"));
//                pagedPageParseRegion.setUrlParseRules(pagedUrlParseRuleList);
//
//                pagePageParseRegionList.add(pagedPageParseRegion);
//
//                urlPageParseRuleMap.put(url, pagePageParseRegionList);
//            }
//
//        }
//        return pagePageParseRegionList;
    }
}
