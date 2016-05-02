package yaycrawler.common.dao.repositories;

import org.springframework.data.repository.CrudRepository;
import yaycrawler.common.domain.PageSite;

/**
 * Created by yuananyun on 2016/5/2.
 */
public interface PageSiteRepository extends CrudRepository<PageSite, String> {
    PageSite findByDomain(String domain);
}
