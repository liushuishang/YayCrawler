package yaycrawler.master.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.stereotype.Service;
import yaycrawler.common.model.CrawRequest;
import yaycrawler.master.model.WorkInfo;
import yaycrawler.master.repository.WorkInfoRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2016/5/11.
 */

@Service
public class WorkInfoService {

    private static final Logger logger = LoggerFactory.getLogger(WorkInfoService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WorkInfoRepository workInfoRepository;
    private LoadingCache<String,String> cahceBuilder= CacheBuilder
            .newBuilder()
            .build(new CacheLoader<String, String>(){
                @Override
                public String load(String key) throws Exception {
//                    String strProValue="hello "+key+"!";
                    return key;
                }

            });

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";

    public Object regedit(WorkInfo params, HttpServletRequest request) {
        String ip = getIpAddress(request);
        params.setClientIp(ip);
        cahceBuilder.put(params.getName(), JSON.toJSONString(params));
        try {
            System.out.println(cahceBuilder.get(params.getName()));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        workInfoRepository.save(params);
        return params;
    }

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


    public Object regeditWork(CrawRequest params, HttpServletRequest request) {
        String field = DigestUtils.sha1DigestAsHex(params.getUrl());
        HashOperations hashOperations = redisTemplate.opsForHash();
        String value = JSON.toJSONString(params);
        hashOperations.put((ITEM_PREFIX + params.getDomain()), field, value);
        return params;
    }
}
