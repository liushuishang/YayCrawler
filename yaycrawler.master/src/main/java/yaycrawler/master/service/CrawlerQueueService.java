package yaycrawler.master.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.stereotype.Service;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.WorkerHeartbeat;

import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Administrator on 2016/5/11.
 */

@Service
public class CrawlerQueueService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerQueueService.class);


    private Long count;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";
    private static final String RUNNING_PREFIX = "running_";
    private static final String FAIL_PREFIX = "fail_";
    private static final String SUCCESS_PREFIX = "success_";

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

    protected String getSetKey() {
        return SET_PREFIX + "data";
    }

    protected String getQueueKey() {
        return QUEUE_PREFIX + "data";
    }

    protected String getRunningKey() {
        return RUNNING_PREFIX + "data";
    }

    protected String getFailKey() {
        return FAIL_PREFIX + "data";
    }

    protected String getItemKey() {
        return ITEM_PREFIX + "data";
    }

    protected String getSuccessKey() {
        return SUCCESS_PREFIX + "data";
    }

    protected String getRandomUrl(CrawlerRequest crawlerRequest) {
        if (crawlerRequest.getData() == null || crawlerRequest.getData().size() == 0)
            return crawlerRequest.getUrl();
        StringBuilder urlBuilder = new StringBuilder(crawlerRequest.getUrl().trim());
        String random = DigestUtils.sha1DigestAsHex(JSON.toJSONString(crawlerRequest.getData()));
        urlBuilder.append(String.format("%s%s=%s", urlBuilder.indexOf("?") > 0 ? "&" : "?", "random", random));
        return urlBuilder.toString();
    }

    public boolean regeditQueues(List<CrawlerRequest> crawlerRequests) {
        try {
            for (CrawlerRequest crawlerRequest:crawlerRequests) {
                Map<String, String> parameter = crawlerRequest.getData();
                List<Object> arrayTmps = null;
                Map pagination = null;
                List datas = Lists.newArrayList();
                if(parameter == null) {
                    regeditQueue(crawlerRequest);
                    continue;
                }
                for (String key : parameter.keySet()) {
                    if (StringUtils.startsWith(key, "$array_")) {
                        arrayTmps = JSON.parseObject(crawlerRequest.getData().get(key).toString(), List.class);
                        List<Map> tmpData = Lists.newArrayList();
                        String tmpParam = StringUtils.substringAfter(key, "$array_");
                        for (Object tmp : arrayTmps) {
                            tmpData.add(ImmutableMap.of(tmpParam, tmp));
                        }

                        datas.add(ImmutableSet.copyOf(tmpData));
                    } else if (StringUtils.startsWith(key, "$pagination_")) {
                        pagination = JSON.parseObject(crawlerRequest.getData().get(key).toString(), Map.class);
                        List<Map> tmpData = Lists.newArrayList();
                        int start = Integer.parseInt(pagination.get("START").toString());
                        int end = Integer.parseInt(pagination.get("END").toString());
                        int step = Integer.parseInt(pagination.get("STEP").toString());
                        String tmpParam = StringUtils.substringAfter(key, "$pagination_");
                        for (int i = start; i <= end; i = i + step) {
                            tmpData.add(ImmutableMap.of(tmpParam, i));
                        }
                        datas.add(ImmutableSet.copyOf(tmpData));
                    } else {
                        datas.add(ImmutableSet.of(ImmutableMap.of(key,parameter.get(key))));
                    }
                }
                Set<List<ImmutableMap<String, String>>> sets = Sets.cartesianProduct(datas);
                for (List<ImmutableMap<String,String>> requests : sets) {
                    CrawlerRequest request = new CrawlerRequest();
                    BeanUtils.copyProperties(crawlerRequest,request);
                    Map<Object,Object> tmp = Maps.newHashMap();
                    for (Map data:requests) {
                        if(data != null)
                            tmp.put(data.keySet().iterator().next(),data.values().iterator().next());
                    }
                    if(StringUtils.equalsIgnoreCase(crawlerRequest.getMethod(),"GET")) {
                        StringBuilder urlBuilder = new StringBuilder(request.getUrl());
                        for (Map.Entry<Object, Object> entry : tmp.entrySet()) {
                            try {
                                if(entry.getKey() == null || StringUtils.isEmpty(entry.getKey().toString())) {
                                    urlBuilder.append(String.format("%s/%s", "/",URLEncoder.encode(String.valueOf(entry.getValue()), "utf-8")));
                                } else
                                    urlBuilder.append(String.format("%s%s=%s",  urlBuilder.indexOf("?") > 0 ? "&" : "?", entry.getKey(), URLEncoder.encode(String.valueOf(entry.getValue()), "utf-8")));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        request.setUrl(urlBuilder.toString());
                        request.setData(null);
                    } else {
                        request.setData(tmp);
                    }
                    regeditQueue(request);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Object regeditQueue(CrawlerRequest crawlerRequest) {
        boolean isDuplicate = isDuplicate(crawlerRequest);
        String queue = getQueueKey();
        String key = getItemKey();
        if (!isDuplicate) {
            CrawlerRequest request = new CrawlerRequest();
            BeanUtils.copyProperties(crawlerRequest,request);
            String url = getRandomUrl(request);
            String field = DigestUtils.sha1DigestAsHex(url);
            request.setHashCode(field);
            HashOperations hashOperations = redisTemplate.opsForHash();
            ListOperations listOperations = redisTemplate.opsForList();
            request.setUrl(url);
            listOperations.leftPush(queue, field);
            String value = JSON.toJSONString(request);
            hashOperations.put(key, field, value);
        }
        return true;
    }

    public boolean isDuplicate(CrawlerRequest request) {
        SetOperations setOperations = redisTemplate.opsForSet();
        String uniqQueue = getSetKey();
        String url = getRandomUrl(request);
        String field = DigestUtils.sha1DigestAsHex(url);
        boolean isDuplicate = setOperations.isMember(uniqQueue, field);
        if (!isDuplicate) {
//            String url = getRandomUrl(request);
//            String field = DigestUtils.sha1DigestAsHex(url.trim());
            setOperations.add(uniqQueue, field);
        }
        return isDuplicate;
    }

    public List<CrawlerRequest> listQueues(long count) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        List<CrawlerRequest> crawlerRequests = new ArrayList<>();
        String queue = getQueueKey();
        String key = getItemKey();
        long size = listOperations.size(queue);
        if (size == 0)
            return crawlerRequests;
        List<String> queueStr = listOperations.range(queue, 0, count - 1);
        for (String field : queueStr) {
            Object data = hashOperations.get(key, field);
            if (data == null)
                continue;
            CrawlerRequest crawlerRequest = JSON.parseObject(data.toString(), CrawlerRequest.class);
            crawlerRequests.add(crawlerRequest);
        }
        return crawlerRequests;
    }

    public void moveRunningQueue(WorkerHeartbeat workerHeartbeat, List<CrawlerRequest> crawlerRequests) {
        long startTime = System.currentTimeMillis();
        HashOperations hashOperations = redisTemplate.opsForHash();
        String runningQueue = getRunningKey();
        String key = getItemKey();
        String queue = getQueueKey();
        ListOperations listOperations = redisTemplate.opsForList();
        for (CrawlerRequest crawlerRequest : crawlerRequests) {
            crawlerRequest.setStartTime(startTime);
            crawlerRequest.setWorkerId(workerHeartbeat.getWorkerId());
            String field = crawlerRequest.getHashCode();
            String value = JSON.toJSONString(crawlerRequest);
            hashOperations.put(runningQueue, field, value);
            hashOperations.delete(key, field);
            listOperations.remove(queue, 0, field);
        }
    }

    public void moveFailQueue(String field,String message) {
        long startTime = System.currentTimeMillis();
        HashOperations hashOperations = redisTemplate.opsForHash();
        String failQueue = getFailKey();
        String key = getRunningKey();
        String data = hashOperations.get(key, field) != null ? hashOperations.get(key, field).toString() : null;
        if (StringUtils.isEmpty(data))
            return;
        CrawlerRequest crawlerRequest = JSON.parseObject(data, CrawlerRequest.class);
        crawlerRequest.setStartTime(startTime);
        crawlerRequest.setMessage(message);
        String value = JSON.toJSONString(crawlerRequest);
        hashOperations.put(failQueue, field, value);
        hashOperations.delete(key, field);
    }

    public Object removeCrawler(String field) {
//        String key = getRunningKey();
        SetOperations setOperations = redisTemplate.opsForSet();
        HashOperations hashOperations = redisTemplate.opsForHash();
        String uniqQueue = getSetKey();
        String runningQueue = getRunningKey();
//        setOperations.remove(uniqQueue, field);
        Object data = hashOperations.get(runningQueue, field);
        if (data == null)
            return false;
        CrawlerRequest crawlerRequest = JSON.parseObject(data.toString(), CrawlerRequest.class);
        crawlerRequest.setStartTime(System.currentTimeMillis());
        moveSuccessQueue(crawlerRequest);
        hashOperations.delete(runningQueue, field);
        return false;
    }

    public Object moveSuccessQueue(CrawlerRequest crawlerRequest) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        String successQueue = getSuccessKey();
        hashOperations.put(successQueue, crawlerRequest.getHashCode(), JSON.toJSONString(crawlerRequest));
        return false;
    }

    public Object removeCrawlers(List<String> fields) {
        for (String field : fields) {
            removeCrawler(field);
        }
        return false;
    }

    public void releseQueue(Long leftTime) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        SetOperations setOperations = redisTemplate.opsForSet();
        String key = getRunningKey();
        String uniqQueue = getSetKey();
        List<String> datas = hashOperations.values(key);
        for (String data : datas) {
            CrawlerRequest crawlerRequest = JSON.parseObject(data, CrawlerRequest.class);
            long oldTime = System.currentTimeMillis() - crawlerRequest.getStartTime();
            if (oldTime > leftTime) {
                crawlerRequest.setStartTime(null);
                crawlerRequest.setWorkerId(null);
                removeCrawler(crawlerRequest.getHashCode());
                setOperations.remove(uniqQueue, crawlerRequest.getHashCode());
                regeditQueue(crawlerRequest);
            }
        }

    }

    protected Object getDatas(String name) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        Set set = hashOperations.keys(name);
        List<CrawlerRequest> crawlerRequests = new ArrayList<>();
        List<String> datas = hashOperations.values(name);
        for (String data : datas) {
            CrawlerRequest crawlerRequest = JSON.parseObject(data, CrawlerRequest.class);
            crawlerRequests.add(crawlerRequest);
        }
//        List<Map> datas = hashOperations.multiGet("item_data",set);
        return crawlerRequests;
    }

    public Object queryQueues(String name) {
        SetOperations setOperations = redisTemplate.opsForSet();
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        if (StringUtils.equalsIgnoreCase(name, "quue_data")) {
            long size = listOperations.size("queue_data");
            return listOperations.range("queue_data", 0, size);
        } else if (StringUtils.equalsIgnoreCase(name, "set_data")) {
            return setOperations.members("set_data");
        } else if (StringUtils.equalsIgnoreCase(name, "item_data")) {
            Object datas = getDatas(name);
            return datas;
        } else if (StringUtils.equalsIgnoreCase(name, "fail_data")) {
            Object datas = getDatas(name);
            return datas;
        } else if (StringUtils.equalsIgnoreCase(name, "running_data")) {
            Object datas = getDatas(name);
            return datas;
        } else if (StringUtils.equalsIgnoreCase(name, "success_data")) {
            Object datas = getDatas(name);
            return datas;
        }
        return null;
    }
}
