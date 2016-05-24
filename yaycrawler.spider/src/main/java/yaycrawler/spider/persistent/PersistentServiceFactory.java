package yaycrawler.spider.persistent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/23.
 */
@Component
public class PersistentServiceFactory {

    @Autowired
    private List<IResultPersistentService> persistentServiceList;

    public IResultPersistentService getPersistentServiceByDataType(String dataType) {
        for (IResultPersistentService resultPersistentService : persistentServiceList) {
            if (resultPersistentService.getSupportedDataType().equals(dataType))
                return resultPersistentService;
        }
        return null;
    }
}
