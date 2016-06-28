package yaycrawler.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.SiteAccount;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/18.
 */
@Repository
public interface SiteAccountRepository extends CrudRepository<SiteAccount, String> {
    Page<SiteAccount> findAll(Pageable pageable);


    @Query(value = "select *  from res_site_account where domain=? and available=1  limit 1", nativeQuery = true)
    SiteAccount findOneByDomain(String domain);
}
