package yaycrawler.dao.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.SiteCookie;

/**
 * Created by ucs_yuananyun on 2016/5/18.
 */
@Repository
public interface SiteCookieRepository extends CrudRepository<SiteCookie, String> {


}
