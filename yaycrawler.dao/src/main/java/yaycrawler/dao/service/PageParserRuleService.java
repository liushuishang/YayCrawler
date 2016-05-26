package yaycrawler.dao.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public boolean savePageInfo(PageInfo pageInfo) {
        if(pageInfo==null) return false;
        return pageInfoRepository.save(pageInfo)!=null;
    }
    public PageInfo getPageInfoById(String pageId) {
        return pageInfoRepository.findOne(pageId);
    }
    public boolean deletePageInfoByIds(List<String> deleteIds) {
        if (deleteIds == null||deleteIds.size()==0) return true;
        for (String deleteId : deleteIds) {
            pageInfoRepository.delete(deleteId);
        }
        return true;
    }


    public boolean savePageRegion(PageParseRegion region) {
        return regionRepository.save(region)!=null;
    }

    public PageParseRegion getPageRegionById(String regionId) {
        return regionRepository.findOne(regionId);
    }

    public Object deletePageRegionByIds(List<String> deleteIds) {
        if (deleteIds == null || deleteIds.size() == 0) return true;
        for (String deleteId : deleteIds) {
            regionRepository.delete(deleteId);
        }
        return true;
    }

    public boolean saveFieldParseRule(FieldParseRule fieldParseRule) {
        if(StringUtils.isBlank(fieldParseRule.getRegionId())) return false;
        return fieldParseRuleRepository.save(fieldParseRule) != null;
    }
    public Object deleteFieldRuleByIds(List<String> deleteIds) {
        if (deleteIds == null || deleteIds.size() == 0) return true;
        for (String deleteId : deleteIds) {
            fieldParseRuleRepository.delete(deleteId);
        }
        return true;
    }

    public boolean saveUrlParseRule(UrlParseRule urlParseRule) {
        if(StringUtils.isBlank(urlParseRule.getRegionId())) return false;
        return urlParseRuleRepository.save(urlParseRule) != null;
    }
    public Object deleteUrlRuleByIds(List<String> deleteIds) {
        if (deleteIds == null || deleteIds.size() == 0) return true;
        for (String deleteId : deleteIds) {
            urlParseRuleRepository.delete(deleteId);
        }
        return true;
    }


}
