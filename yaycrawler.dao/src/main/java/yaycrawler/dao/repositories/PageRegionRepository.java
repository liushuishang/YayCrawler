package yaycrawler.dao.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.PageParseRegion;

/**
 * Created by yuananyun on 2016/5/2.
 */
@Repository
public interface PageRegionRepository extends CrudRepository<PageParseRegion, String> {

//    List<PageParseRegion> findByPageUrl(String pageUrl);

    PageParseRegion findOneByPageIdAndSelectExpression(String pageId, String selectExpression);

//    @Query(value = "select field.id as id, region.pageUrl,region.name,region.selectExpression,'field' as ruleType,field.fieldName,field.rule from PageParseRegion region,FieldParseRule field WHERE (field.regionId=region.id)")
//    List<Map<String,Object>> queryAllFieldRules();
//
//
//    @Query("select url.id as id, region.pageUrl,region.name,region.selectExpression,'childUrl' as ruleType,'' as fieldName,url.rule from PageParseRegion region,UrlParseRule url WHERE (url.regionId=region.id)")
//    List<Map<String,Object>> queryAllUrlRules();
//
//    @Query("select field.id as id,  region.pageUrl,region.name,region.selectExpression,'field' as ruleType,field.fieldName,field.rule from PageParseRegion region,FieldParseRule field WHERE (field.regionId=region.id) and region.pageUrl=?1")
//    List<Map<String,Object>> queryFieldRulesByUrl(String url);
//
//    @Query("select url.id as id, region.pageUrl,region.name,region.selectExpression,'childUrl' as ruleType,'' as fieldName,url.rule from PageParseRegion region,UrlParseRule url WHERE (url.regionId=region.id) and region.pageUrl=?1")
//    List<Map<String,Object>> queryUrlRulesByUrl(String url);
}
