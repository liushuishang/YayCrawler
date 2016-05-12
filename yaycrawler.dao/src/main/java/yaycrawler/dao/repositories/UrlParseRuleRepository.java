package yaycrawler.dao.repositories;


import org.springframework.data.repository.CrudRepository;
import yaycrawler.dao.domain.UrlParseRule;

import java.util.List;

/**
 * Created by yuananyun on 2016/5/2.
 */
public interface UrlParseRuleRepository extends CrudRepository<UrlParseRule, String> {
    List<UrlParseRule> findByRegionId(String regionId);
}
