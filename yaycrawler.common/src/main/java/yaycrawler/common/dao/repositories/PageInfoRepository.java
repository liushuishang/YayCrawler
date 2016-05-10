package yaycrawler.common.dao.repositories;

import org.springframework.data.repository.CrudRepository;
import yaycrawler.common.domain.PageInfo;

/**
 * Created by ucs_yuananyun on 2016/5/10.
 */
public interface PageInfoRepository extends CrudRepository<PageInfo, String> {

    PageInfo findOneByPageUrl(String pageUr);



}
