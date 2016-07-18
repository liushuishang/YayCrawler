package yaycrawler.dao.repositories;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.FieldParseRule;

import java.util.List;

/**
 * Created by yuananyun on 2016/5/2.
 */
@Repository
public interface FieldParseRuleRepository extends CrudRepository<FieldParseRule, String> {

    List<FieldParseRule> findByRegionId(String regionId);
}
