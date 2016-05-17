package yaycrawler.master.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.stereotype.Service;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.master.communication.WorkerActor;

import javax.servlet.http.HttpServletRequest;

import java.util.List;


/**
 * Created by Administrator on 2016/5/11.
 */

@Service
public class WorkInfoService {

    private static final Logger logger = LoggerFactory.getLogger(WorkInfoService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";
    private static final String RUNNING_PREFIX = "running_";
    private static final String SPIDER_DATA = "spider_data";

    public String getIpAddress(HttpServletRequest request) {
        //
        String ip = request.getHeader("X-Forwarded-For");
        if (logger.isInfoEnabled()) {
            logger.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
                }
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

//    protected String getSetKey(String domain) {
//        return SET_PREFIX + domain;
//    }
//
//    protected String getQueueKey(String domain) {
//        return QUEUE_PREFIX + domain;
//    }
//
//    protected String getRunningKey(String domain) {
//        return RUNNING_PREFIX + domain;
//    }

    public Object regeditWorks(List<CrawlerRequest> crawlerRequests) {
        for (CrawlerRequest crawlerRequest : crawlerRequests) {
            regeditWork(crawlerRequest);
        }
        return crawlerRequests;
    }

    public Object regeditWork(CrawlerRequest crawlerRequest) {
        boolean isDuplicate = isDuplicate(crawlerRequest);
        String queue = QUEUE_PREFIX + "data";
        String key = ITEM_PREFIX + "data";
        if (!isDuplicate) {
            String field = DigestUtils.sha1DigestAsHex(crawlerRequest.getUrl());
            crawlerRequest.setHashCode(field);
            HashOperations hashOperations = redisTemplate.opsForHash();
            ListOperations listOperations = redisTemplate.opsForList();
            listOperations.leftPush(queue, crawlerRequest.getUrl());
            String value = JSON.toJSONString(crawlerRequest);
            hashOperations.put(key, field, value);
        }
        return crawlerRequest;
    }

    public boolean isDuplicate(CrawlerRequest request) {
        SetOperations setOperations = redisTemplate.opsForSet();
        String uniqQueue = SET_PREFIX + "data";
        boolean isDuplicate = setOperations.isMember(uniqQueue, request.getUrl());
        if (!isDuplicate) {
            String url = request.getUrl();
            String field = DigestUtils.sha1DigestAsHex(url.trim());
            setOperations.add(uniqQueue,field);
        }
        return isDuplicate;
    }

    public List<CrawlerRequest> listWorks(String workId, long count) {
        return null;
    }

    public List<CrawlerRequest> listWorks(long count) {
        SetOperations setOperations = redisTemplate.opsForSet();
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        List<CrawlerRequest> crawlerRequests = Lists.newArrayList();
        String queue = QUEUE_PREFIX + "data";
        String key = ITEM_PREFIX + "data";
        for (int i = 0; i < count; i++) {
            Object url = listOperations.leftPop(queue);
            if (url != null) {
                String field = DigestUtils.sha1DigestAsHex(url.toString());
                String data = hashOperations.get(key, field).toString();
                CrawlerRequest crawlerRequest = JSON.parseObject(data, CrawlerRequest.class);
                crawlerRequests.add(crawlerRequest);
            }
        }
//        workerActor.assignTasks(crawlerRequests);
        return crawlerRequests;
    }

    public void moveRunningQueue(List<CrawlerRequest> crawlerRequests) {
        long startTime = System.currentTimeMillis();
        HashOperations hashOperations = redisTemplate.opsForHash();
        String runningQueue = RUNNING_PREFIX + "data";
        String key = ITEM_PREFIX + "data";
        for (CrawlerRequest crawlerRequest : crawlerRequests) {
            crawlerRequest.setStartTime(startTime);
            String field = DigestUtils.sha1DigestAsHex(crawlerRequest.getUrl());
            String value = JSON.toJSONString(crawlerRequest);
            hashOperations.put(runningQueue, field, value);
            hashOperations.delete(key,field);
        }

    }

    public Object removeCrawler(String field) {
        String key = ITEM_PREFIX + "data";
        SetOperations setOperations = redisTemplate.opsForSet();
        HashOperations hashOperations = redisTemplate.opsForHash();
        String uniqQueue = SET_PREFIX + "data";
        String runningQueue = RUNNING_PREFIX + "data";
        hashOperations.delete(key, field);
        setOperations.remove(uniqQueue, field);
        hashOperations.delete(runningQueue,field);
        return false;
    }

    public Object removeCrawlers(List<String> fields) {

        for (String field : fields) {
            removeCrawler(field);
        }
        return false;
    }
}
