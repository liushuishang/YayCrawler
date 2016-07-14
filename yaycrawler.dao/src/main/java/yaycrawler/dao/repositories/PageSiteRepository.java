package yaycrawler.dao.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.PageSite;

/**
 * Created by yuananyun on 2016/5/2.
 */
@Repository
public interface PageSiteRepository extends CrudRepository<PageSite, String> {
    PageSite findByDomain(String domain);
    Page<PageSite> findAllByOrderByDomainAsc(Pageable pageable);
}
