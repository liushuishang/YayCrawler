package yaycrawler.master.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import yaycrawler.common.model.CrawRequest;
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

    private LoadingCache<String, String> cahceBuilder = CacheBuilder
            .newBuilder()
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
//                    String strProValue="hello "+key+"!";
                    return key;
                }

            });

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";
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

    protected String getSetKey(String domain) {
        return SET_PREFIX + domain;
    }

    protected String getQueueKey(String domain) {
        return QUEUE_PREFIX + domain;
    }

    public Object regeditWorks(List<CrawRequest> crawRequests) {
        for (CrawRequest crawRequest:crawRequests) {
            regeditWork(crawRequest);
        }
        return crawRequests;
    }

    public Object regeditWork(CrawRequest crawRequest) {
        boolean isDuplicate = isDuplicate(crawRequest);
        if(!isDuplicate) {
            String field = DigestUtils.sha1DigestAsHex(crawRequest.getUrl());
            HashOperations hashOperations = redisTemplate.opsForHash();
            ListOperations listOperations = redisTemplate.opsForList();
            listOperations.leftPush(getQueueKey(crawRequest.getDomain()), crawRequest.getUrl());
            String value = JSON.toJSONString(crawRequest);
            hashOperations.put((ITEM_PREFIX + crawRequest.getDomain()), field, value);
            SetOperations setOperations = redisTemplate.opsForSet();
            setOperations.add(SPIDER_DATA,crawRequest.getDomain());
        }
//        listWorks(10);
        return crawRequest;
    }

    public boolean isDuplicate(CrawRequest request) {
        SetOperations setOperations = redisTemplate.opsForSet();
        boolean isDuplicate = setOperations.isMember(getSetKey(request.getDomain()), request.getUrl());
        if (!isDuplicate) {
            String url = request.getUrl();
            setOperations.add(getSetKey(request.getDomain()),url);
        }
        return isDuplicate;
    }

    public List<CrawRequest> listWorks(long count) {
        SetOperations setOperations = redisTemplate.opsForSet();
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        List<CrawRequest> crawRequests = Lists.newArrayList();
        String domain = setOperations.pop(SPIDER_DATA).toString();
        String key = ITEM_PREFIX + domain;
        for (int i = 0; i < count; i++) {
            Object url = listOperations.leftPop(getQueueKey(domain));
            String field = DigestUtils.sha1DigestAsHex(url.toString());
            String data = hashOperations.get(key,field).toString();
            CrawRequest crawRequest = JSON.parseObject(data, CrawRequest.class);
            crawRequests.add(crawRequest);
        }
        return crawRequests;
    }
}
