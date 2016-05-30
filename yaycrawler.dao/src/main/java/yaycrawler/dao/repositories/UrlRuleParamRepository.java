package yaycrawler.dao.repositories;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.UrlRuleParam;

/**
 * Created by yuananyun on 2016/5/2.
 */
@Repository
public interface UrlRuleParamRepository extends CrudRepository<UrlRuleParam, String> {

}
