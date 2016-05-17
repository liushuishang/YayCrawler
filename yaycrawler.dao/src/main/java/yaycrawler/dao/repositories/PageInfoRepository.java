package yaycrawler.dao.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.PageInfo;


/**
 * Created by ucs_yuananyun on 2016/5/10.
 */
@Repository
public interface PageInfoRepository extends CrudRepository<PageInfo, String> {

    PageInfo findOneByPageUrl(String pageUr);



}
