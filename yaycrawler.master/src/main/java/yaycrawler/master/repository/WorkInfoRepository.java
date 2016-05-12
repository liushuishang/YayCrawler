package yaycrawler.master.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.master.model.WorkInfo;

/**
 * Created by Administrator on 2016/5/11.
 */

@Repository
public interface WorkInfoRepository extends JpaRepository<WorkInfo,Long> {

}
