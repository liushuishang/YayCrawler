package yaycrawler.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.PageSite;
import yaycrawler.dao.domain.SiteAccount;
import yaycrawler.dao.domain.SiteCookie;

/**
 * Created by ucs_yuananyun on 2016/5/18.
 */
@Repository
public interface SiteAccountRepository extends CrudRepository<SiteAccount, String> {
    Page<SiteAccount> findAll(Pageable pageable);
}
