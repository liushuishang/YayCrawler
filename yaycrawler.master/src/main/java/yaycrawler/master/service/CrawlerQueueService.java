package yaycrawler.master.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.TasksResult;
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
    private static final String ITEM_QUEUE_PREFIX = "item_queue_";
    private static final String RUNNING_PREFIX = "running_";
    private static final String RUNNING_QUEUE_PREFIX = "running_queue_";
    private static final String FAIL_PREFIX = "fail_";
    private static final String FAIL_QUEUE_PREFIX = "fail_queue_";
    private static final String SUCCESS_PREFIX = "success_";
    private static final String SUCCESS_QUEUE_PREFIX = "success_queue_";

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

    protected String getRunningQueueKey() {
        return RUNNING_QUEUE_PREFIX + "data";
    }

    protected String getRunningKey() {
        return RUNNING_PREFIX + "data";
    }

    protected String getFailKey() {
        return FAIL_PREFIX + "data";
    }

    protected String getFailQueueKey() {
        return FAIL_QUEUE_PREFIX + "data";
    }

    protected String getItemKey() {
        return ITEM_PREFIX + "data";
    }

    protected String getItemQueueKey() {
        return ITEM_QUEUE_PREFIX + "data";
    }

    protected String getSuccessKey() {
        return SUCCESS_PREFIX + "data";
    }

    protected String getSuccessQueueKey() {
        return SUCCESS_QUEUE_PREFIX + "data";
    }

    protected String getRandomUrl(CrawlerRequest crawlerRequest) {
        if (crawlerRequest.getData() == null || crawlerRequest.getData().size() == 0)
            return crawlerRequest.getUrl();
        StringBuilder urlBuilder = new StringBuilder(crawlerRequest.getUrl().trim());
        String random = DigestUtils.sha1Hex(JSON.toJSONString(crawlerRequest.getData()));
        urlBuilder.append(String.format("%s%s=%s", urlBuilder.indexOf("?") > 0 ? "&" : "?", "random", random));
        return urlBuilder.toString();
    }

    public boolean regeditQueues(List<CrawlerRequest> crawlerRequests,boolean removeDuplicated) {
        try {
            logger.info("开始注册{}个任务", crawlerRequests.size());
            for (CrawlerRequest crawlerRequest : crawlerRequests) {
                Map<String, String> parameter = crawlerRequest.getData();
                List<Object> arrayTmps = null;
                Map pagination = null;
                List datas = Lists.newArrayList();
                if (parameter == null && parameter.size() == 0) {
                    regeditQueue(crawlerRequest,removeDuplicated);
                    continue;
                }
                for (String key : parameter.keySet()) {
                    if (StringUtils.startsWith(key, "$array_")) {
                        arrayTmps = JSON.parseObject(parameter.get(key).toString(), List.class);
                        List<Map> tmpData = Lists.newArrayList();
                        String tmpParam = StringUtils.substringAfter(key, "$array_");
                        for (Object tmp : arrayTmps) {
                            tmpData.add(ImmutableMap.of(tmpParam, tmp));
                        }

                        datas.add(ImmutableSet.copyOf(tmpData));
                    } else if (StringUtils.startsWith(key, "$pagination_")) {
                        pagination = JSON.parseObject(parameter.get(key).toString(), Map.class);
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
                        datas.add(ImmutableSet.of(ImmutableMap.of(key, parameter.get(key))));
                    }
                }
                Set<List<ImmutableMap<String, String>>> sets = Sets.cartesianProduct(datas);
                for (List<ImmutableMap<String, String>> requests : sets) {
                    CrawlerRequest request = new CrawlerRequest();
                    BeanUtils.copyProperties(crawlerRequest, request);
                    Map<Object, Object> tmp = Maps.newHashMap();
                    for (Map data : requests) {
                        if (data != null)
                            tmp.put(data.keySet().iterator().next(), data.values().iterator().next());
                    }
                    if (StringUtils.equalsIgnoreCase(crawlerRequest.getMethod(), "GET")) {
                        StringBuilder urlBuilder = new StringBuilder(request.getUrl());
                        for (Map.Entry<Object, Object> entry : tmp.entrySet()) {
                            try {
                                if (entry.getKey() == null || StringUtils.isEmpty(entry.getKey().toString())) {
                                    urlBuilder.append(String.format("%s/%s", "/", URLEncoder.encode(String.valueOf(entry.getValue()), "utf-8")));
                                } else
                                    urlBuilder.append(String.format("%s%s=%s", urlBuilder.indexOf("?") > 0 ? "&" : "?", entry.getKey(), URLEncoder.encode(String.valueOf(entry.getValue()), "utf-8")));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        request.setUrl(urlBuilder.toString());
                        request.setData(null);
                    } else {
                        request.setData(tmp);
                    }
                    regeditQueue(request,removeDuplicated);
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public boolean regeditQueue(CrawlerRequest crawlerRequest) {
       return regeditQueue(crawlerRequest,false);
    }

    public boolean regeditQueue(CrawlerRequest crawlerRequest,boolean removeDuplicated) {
        boolean isDuplicate = removeDuplicated ?false: isDuplicate(crawlerRequest);
        if (isDuplicate) return false;

        try {
            String queue = getQueueKey();
            String key = getItemKey();
            String itemQueue = getItemQueueKey();

            CrawlerRequest request = new CrawlerRequest();
            BeanUtils.copyProperties(crawlerRequest, request);
            String url = getRandomUrl(request);
            String field = DigestUtils.sha1Hex(url);
            request.setHashCode(field);
            HashOperations hashOperations = redisTemplate.opsForHash();
            ListOperations listOperations = redisTemplate.opsForList();
            request.setUrl(url);
            String value = JSON.toJSONString(request);
            if (!hashOperations.hasKey(key, field)) {
                listOperations.leftPush(itemQueue, field);
            }
            hashOperations.put(key, field, value);
            listOperations.leftPush(queue, field);
            return true;
        } catch (Exception ex) {
            logger.info("任务{}注册失败！错误：{}", crawlerRequest.toString(), ex.getMessage());
            return false;
        }
    }

    public boolean isDuplicate(CrawlerRequest request) {
        SetOperations setOperations = redisTemplate.opsForSet();
        String uniqQueue = getSetKey();
        String url = getRandomUrl(request);
        String field = DigestUtils.sha1Hex(url);
        boolean isDuplicate = setOperations.isMember(uniqQueue, field);
        if (!isDuplicate) {
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
            if (!hashOperations.hasKey(key, field))
                continue;
            String data = String.valueOf(hashOperations.get(key, field));
            CrawlerRequest crawlerRequest = JSON.parseObject(data, CrawlerRequest.class);
            crawlerRequests.add(crawlerRequest);
        }
        return crawlerRequests;
    }

    public void moveRunningQueue(WorkerHeartbeat workerHeartbeat, List<CrawlerRequest> crawlerRequests) {
        long startTime = System.currentTimeMillis();
        HashOperations hashOperations = redisTemplate.opsForHash();
        String runningQueue = getRunningKey();
        String key = getItemKey();
        String itemQueue = getItemQueueKey();
        String runningKey = getRunningQueueKey();
        String queue = getQueueKey();
        ListOperations listOperations = redisTemplate.opsForList();
        for (CrawlerRequest crawlerRequest : crawlerRequests) {
            crawlerRequest.setStartTime(startTime);
            crawlerRequest.setWorkerId(workerHeartbeat.getWorkerId());
            String field = crawlerRequest.getHashCode();
            String value = JSON.toJSONString(crawlerRequest);
            if (!hashOperations.hasKey(runningQueue, field)) {
                listOperations.leftPush(runningKey, field);
            }
            hashOperations.put(runningQueue, field, value);
            hashOperations.delete(key, field);
            if (!hashOperations.hasKey(key, field)) {
                listOperations.remove(itemQueue, 0, field);
                listOperations.remove(queue, 0, field);
            }
        }
    }

    public void moveFailQueue(String field, String message) {
        long startTime = System.currentTimeMillis();
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        String failQueue = getFailKey();
        String failKey = getFailQueueKey();
        String key = getRunningKey();
        String runningKey = getRunningQueueKey();
        if (!hashOperations.hasKey(key, field))
            return;
        String data = String.valueOf(hashOperations.get(key, field));
        CrawlerRequest crawlerRequest = JSON.parseObject(data, CrawlerRequest.class);
        crawlerRequest.setStartTime(startTime);
        crawlerRequest.setMessage(message);
        String value = JSON.toJSONString(crawlerRequest);
        if (!hashOperations.hasKey(failQueue, field)) {
            listOperations.leftPush(failKey, field);
        }
        hashOperations.put(failQueue, field, value);
        hashOperations.delete(key, field);
        listOperations.remove(runningKey, 0, field);
    }

    public Object removeCrawler(String field) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        String runningQueue = getRunningKey();
        String runningKey = getRunningQueueKey();
        if (!hashOperations.hasKey(runningQueue, field))
            return false;
        String data = String.valueOf(hashOperations.get(runningQueue, field));
        CrawlerRequest crawlerRequest = JSON.parseObject(data, CrawlerRequest.class);
        crawlerRequest.setStartTime(System.currentTimeMillis());
        moveSuccessQueue(crawlerRequest);
        hashOperations.delete(runningQueue, field);
        listOperations.remove(runningKey, 0, field);
        return false;
    }

    public Object moveSuccessQueue(CrawlerRequest crawlerRequest) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        String successQueue = getSuccessKey();
        String successKey = getSuccessQueueKey();
        if (!hashOperations.hasKey(successQueue, crawlerRequest.getHashCode())) {
            listOperations.leftPush(successKey, crawlerRequest.getHashCode());
        }
        hashOperations.put(successQueue, crawlerRequest.getHashCode(), JSON.toJSONString(crawlerRequest));
        return false;
    }

    public Object removeCrawlers(List<String> fields) {
        for (String field : fields) {
            removeCrawler(field);
        }
        return false;
    }

    /**
     * 刷新超时队列（把超时的运行中队列任务重新加入待执行队列）
     *
     * @param leftTime
     */
    public void refreshBreakedQueue(Long leftTime) {
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

    public Object queryQueues(TasksResult tasksResult) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        List<CrawlerRequest> crawlerRequests = new ArrayList<>();
        List<String> datas = null;
        String itemQueue = getItemKey();
        String itemKey = getItemQueueKey();
        String runningQueue = getRunningKey();
        String runningKey = getRunningQueueKey();
        String successQueue = getSuccessKey();
        String successKey = getSuccessQueueKey();
        String failQueue = getFailKey();
        String failKey = getFailQueueKey();
        String name = tasksResult.getName();
        List<String> keys = null;
        String key = "";
        String hashKey = "";
        int pageIndex = tasksResult.getPageIndex();
        int pageSize = tasksResult.getPageSize();
        if (StringUtils.equalsIgnoreCase(name, "item")) {
            key = itemKey;
            hashKey = itemQueue;
        } else if (StringUtils.equalsIgnoreCase(name, "running")) {
            key = runningKey;
            hashKey = runningQueue;
        } else if (StringUtils.equalsIgnoreCase(name, "success")) {
            key = successKey;
            hashKey = successQueue;
        } else if (StringUtils.equalsIgnoreCase(name, "fail")) {
            key = failKey;
            hashKey = failQueue;
        }
        int page = pageIndex * pageSize;
        int offset = page + (pageSize - 1);
        keys = listOperations.range(key, page, offset);
        datas = hashOperations.multiGet(hashKey, keys);
        tasksResult.setTotal(hashOperations.size(hashKey));
        for (String data : datas) {
            CrawlerRequest crawlerRequest = JSON.parseObject(data, CrawlerRequest.class);
            if (crawlerRequest != null)
                crawlerRequests.add(crawlerRequest);
        }
        tasksResult.setRows(crawlerRequests);
        return tasksResult;
    }

}
