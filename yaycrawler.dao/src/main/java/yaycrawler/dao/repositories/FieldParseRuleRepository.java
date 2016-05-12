package yaycrawler.dao.repositories;


import org.springframework.data.repository.CrudRepository;
import yaycrawler.dao.domain.FieldParseRule;

import java.util.List;

/**
 * Created by yuananyun on 2016/5/2.
 */
public interface FieldParseRuleRepository extends CrudRepository<FieldParseRule, String> {
    List<FieldParseRule> findByRegionId(String regionId);
}
