package yaycrawler.spider.pipeline;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import yaycrawler.spider.persistent.IResultPersistentService;
import yaycrawler.spider.persistent.PersistentDataType;
import yaycrawler.spider.persistent.PersistentServiceFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/11.
 */
@Component
public class GenericPipeline implements Pipeline {
    private static Logger logger = LoggerFactory.getLogger(GenericPipeline.class);

    @Autowired(required = false)
    private PersistentServiceFactory persistentServiceFactory;

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (persistentServiceFactory == null) {
            logger.info("缺失PersistentServiceFactory的实现");
            return;
        }
        try {
            String pageUrl = resultItems.getRequest().getUrl();
            Map<String, Object> pageDataMap = resultItems.getAll();

            /**
             * 先按照dataType分组
             */
            Map<String, Map<String, Object>> groupedRegionDataMap = new HashMap<>();
            for (Map.Entry<String, Object> regionDataMapEntry : pageDataMap.entrySet()) {
                Object regionDataValue = regionDataMapEntry.getValue();
                String dataType = PersistentDataType.MAP;
                if (regionDataValue instanceof Map) {
                    dataType = MapUtils.getString((Map) regionDataValue, "dataType", PersistentDataType.MAP);
                    ((Map) regionDataValue).remove("dataType");
                }
                addToDataTypeGroup(groupedRegionDataMap, dataType, regionDataMapEntry);
            }

            /**
             * 按照数据类型分别进行持久化
             */
            for (Map.Entry<String, Map<String, Object>> groupedDataEntry : groupedRegionDataMap.entrySet()) {
                try {
                    IResultPersistentService persistentService = persistentServiceFactory.getPersistentServiceByDataType(groupedDataEntry.getKey());
                    if (persistentService != null) {
                        logger.info("开始持久化{}到{}",groupedDataEntry.getKey(),persistentService.toString());
                        Map dataMap = groupedDataEntry.getValue();
                        persistentService.saveCrawlerResult(pageUrl, dataMap);
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private void addToDataTypeGroup(Map<String, Map<String, Object>> groupedDataMapList, String dataType, Map.Entry<String, Object> regionDataMap) {
        if (StringUtils.isBlank(dataType)) return;
        Map<String, Object> groupedMap = groupedDataMapList.get(dataType);
        if (groupedMap == null) {
            groupedMap = new HashMap<>();
            groupedDataMapList.put(dataType, groupedMap);
        }
        groupedMap.put(regionDataMap.getKey(), regionDataMap.getValue());
    }


}
