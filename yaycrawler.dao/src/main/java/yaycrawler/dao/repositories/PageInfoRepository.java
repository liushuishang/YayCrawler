package yaycrawler.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.PageInfo;
import yaycrawler.dao.domain.PageSite;

import javax.persistence.OrderBy;


/**
 * Created by ucs_yuananyun on 2016/5/10.
 */
@Repository
public  interface PageInfoRepository extends CrudRepository<PageInfo, String> {

    @Query(value="select *  from conf_page_info pi where ? REGEXP  pi.url_rgx",nativeQuery = true)
    PageInfo findOneByUrlRgx(String url);

//    @OrderBy("createdDate desc ")
    Page<PageInfo> findAllByOrderByCreatedDateDesc(Pageable pageable);


}
