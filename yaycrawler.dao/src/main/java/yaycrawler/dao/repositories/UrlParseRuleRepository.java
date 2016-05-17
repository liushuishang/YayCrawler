package yaycrawler.dao.repositories;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.UrlParseRule;

import java.util.List;

/**
 * Created by yuananyun on 2016/5/2.
 */
@Repository
public interface UrlParseRuleRepository extends CrudRepository<UrlParseRule, String> {
    List<UrlParseRule> findByRegionId(String regionId);
}
