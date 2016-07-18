package yaycrawler.dao.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yaycrawler.dao.domain.*;
import yaycrawler.dao.repositories.*;

import java.util.List;
import java.util.Set;

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
    private UrlRuleParamRepository ruleParamRepository;

    @Autowired
    private PageSiteRepository siteRepository;

    @Autowired
    private SiteAccountRepository accountRepository;

    public static final String DEMO_CACHE_NAME = "demo";

    @Cacheable(value = DEMO_CACHE_NAME, keyGenerator = "wiselyKeyGenerator")
    public Set<PageParseRegion> getPageRegions(String pageUrl) {
        if (StringUtils.isBlank(pageUrl)) return null;

        PageInfo pageInfo = pageInfoRepository.findOneByUrlRgx(pageUrl);
        return  pageInfo.getPageParseRegions();
    }

    public Page<PageSite> querySites(int pageIndex, int pageSize) {
        return siteRepository.findAllByOrderByDomainAsc(new PageRequest(pageIndex, pageSize));
    }

    public Page<SiteAccount> queryAccounts(int pageIndex, int pageSize) {
        return accountRepository.findAll(new PageRequest(pageIndex,pageSize));
    }

    @CacheEvict(value = DEMO_CACHE_NAME)
    public boolean deleteSiteByIds(List<String> deleteIds) {
        for (String deleteId : deleteIds) {
            //先删除cookies
            siteRepository.delete(deleteId);
        }
        return true;
    }

    @CacheEvict(value = DEMO_CACHE_NAME)
    public boolean deleteAccountByIds(List<String> deleteIds) {
        for (String deleteId : deleteIds) {
            //先删除cookies
            accountRepository.delete(deleteId);
        }
        return true;
    }

    @CacheEvict(value = DEMO_CACHE_NAME)
    public boolean addSite(PageSite pageSite) {
        return siteRepository.save(pageSite) != null;
    }

    @CacheEvict(value = DEMO_CACHE_NAME)
    public boolean addAccount(SiteAccount siteAccount) {
        return accountRepository.save(siteAccount) != null;
    }

    public Page<PageInfo> queryPageInfos(int pageIndex, int pageSize) {
        return pageInfoRepository.findAllByOrderByCreatedDateDesc(new PageRequest(pageIndex, pageSize));
    }

    @CacheEvict(value = DEMO_CACHE_NAME)
    public boolean savePageInfo(PageInfo pageInfo) {
        if (pageInfo == null) return false;
        return pageInfoRepository.save(pageInfo) != null;
    }

    public PageInfo getPageInfoById(String pageId) {
        return pageInfoRepository.findOne(pageId);
    }

    @CacheEvict(value = DEMO_CACHE_NAME)
    public PageInfo findOnePageInfoByRgx(String url) {
        if(StringUtils.isBlank(url)) return null;
        return pageInfoRepository.findOneByUrlRgx(url);
    }

    @CacheEvict(value = DEMO_CACHE_NAME)
    public boolean deletePageInfoByIds(List<String> deleteIds) {
        if (deleteIds == null || deleteIds.size() == 0) return true;
        for (String deleteId : deleteIds) {
            pageInfoRepository.delete(deleteId);
        }
        return true;
    }

    @CacheEvict(value = DEMO_CACHE_NAME)
    public boolean savePageRegion(PageParseRegion region) {
        return regionRepository.save(region) != null;
    }

    @Cacheable(value = DEMO_CACHE_NAME, keyGenerator = "wiselyKeyGenerator")
    public PageParseRegion getPageRegionById(String regionId) {
        return regionRepository.findOne(regionId);
    }
    @CacheEvict(value = DEMO_CACHE_NAME)
    public Object deletePageRegionByIds(List<String> deleteIds) {
        if (deleteIds == null || deleteIds.size() == 0) return true;
        for (String deleteId : deleteIds) {
            regionRepository.delete(deleteId);
        }
        return true;
    }
    @CacheEvict(value = DEMO_CACHE_NAME)
    public boolean saveFieldParseRule(FieldParseRule fieldParseRule) {
        if (StringUtils.isBlank(fieldParseRule.getRegionId())) return false;
        return fieldParseRuleRepository.save(fieldParseRule) != null;
    }
    @CacheEvict(value = DEMO_CACHE_NAME)
    public Object deleteFieldRuleByIds(List<String> deleteIds) {
        if (deleteIds == null || deleteIds.size() == 0) return true;
        for (String deleteId : deleteIds) {
            fieldParseRuleRepository.delete(deleteId);
        }
        return true;
    }
    @CacheEvict(value = DEMO_CACHE_NAME)
    public boolean saveUrlParseRule(UrlParseRule urlParseRule) {
        if (StringUtils.isBlank(urlParseRule.getRegionId())) return false;
        return urlParseRuleRepository.save(urlParseRule) != null;
    }
    @CacheEvict(value = DEMO_CACHE_NAME)
    public Object deleteUrlRuleByIds(List<String> deleteIds) {
        if (deleteIds == null || deleteIds.size() == 0) return true;
        for (String deleteId : deleteIds) {
            urlParseRuleRepository.delete(deleteId);
        }
        return true;
    }

    @Cacheable(value = DEMO_CACHE_NAME, keyGenerator = "wiselyKeyGenerator")
    public UrlParseRule getUrlParseRuleById(String urlRuleId) {
        return urlParseRuleRepository.findOne(urlRuleId);
    }
    @CacheEvict(value = DEMO_CACHE_NAME)
    public Boolean saveUrlRuleParam(UrlRuleParam urlRuleParam) {
        if (urlRuleParam == null) return false;
        return ruleParamRepository.save(urlRuleParam) != null;
    }
    @CacheEvict(value = DEMO_CACHE_NAME)
    public Object deleteUrlRuleParamByIds(List<String> deleteIds) {
        if(deleteIds==null||deleteIds.size()==0) return true;
        for (String deleteId : deleteIds) {
            ruleParamRepository.delete(deleteId);
        }
        return true;
    }


}
