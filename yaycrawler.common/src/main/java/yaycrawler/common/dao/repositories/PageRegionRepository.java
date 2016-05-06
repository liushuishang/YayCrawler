package yaycrawler.common.dao.repositories;

import org.springframework.data.repository.CrudRepository;
import yaycrawler.common.domain.PageParseRegion;

import java.util.List;

/**
 * Created by yuananyun on 2016/5/2.
 */
public interface PageRegionRepository extends CrudRepository<PageParseRegion, String> {

    List<PageParseRegion> findByPageUrl(String pageUrl);

    List<PageParseRegion> findByPageUrlAndName(String pageUrl,String name);

}
