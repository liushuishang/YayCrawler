package yaycrawler.dao.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.GeetestTrail;

/**
 * Created by ucs_yuananyun on 2016/6/27.
 */
@Repository
public interface GeetestTrailRepository extends CrudRepository<GeetestTrail, String> {

    @Query(value = "select  *  from res_geetest_trail  where deltaX>? order by deltaX asc limit 1", nativeQuery = true)
    GeetestTrail findOneByDeltaX(int deltaX);
}
