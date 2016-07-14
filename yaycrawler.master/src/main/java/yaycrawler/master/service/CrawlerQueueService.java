package yaycrawler.master.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.common.model.TasksResult;
import yaycrawler.common.model.WorkerHeartbeat;

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
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${task.queue.count}")
    private int taskCount;

    private static final String DUPLICATE_REMOVAL_PREFIX = "duplicateRemoval:set_";

    private static final String INITIALIZATION_PREFIX = "initialization:detailed_";
    private static final String INITIALIZATION_QUEUE_PREFIX = "initialization:queue_";
    private static final String RUNNING_PREFIX = "running:detailed_";
    private static final String RUNNING_QUEUE_PREFIX = "running:queue_";
    private static final String FAIL_PREFIX = "fail:detailed";
    private static final String FAIL_QUEUE_PREFIX = "fail:queue_";
    private static final String SUCCESS_PREFIX = "success:detailed_";
    private static final String SUCCESS_QUEUE_PREFIX = "success:queue_";

    protected String getSetKey() {
        return DUPLICATE_REMOVAL_PREFIX + "data";
    }

//    protected String getQueueKey() {
//        return QUEUE_PREFIX + "data";
//    }

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
        return INITIALIZATION_PREFIX + "data";
    }

    protected String getItemQueueKey() {
        return INITIALIZATION_QUEUE_PREFIX + "data";
    }

    protected String getSuccessKey() {
        return SUCCESS_PREFIX + "data";
    }

    protected String getSuccessQueueKey() {
        return SUCCESS_QUEUE_PREFIX + "data";
    }

    /**
     * 通过参数，生成了唯一的一个URL
     * @param crawlerRequest 请求
     * @return
     */
    protected String getRandomUrl(CrawlerRequest crawlerRequest) {
        if (crawlerRequest.getData() == null || crawlerRequest.getData().size() == 0)
            return crawlerRequest.getUrl();
        StringBuilder urlBuilder = new StringBuilder(crawlerRequest.getUrl().trim());
        String random = DigestUtils.sha1Hex(JSON.toJSONString(crawlerRequest.getData()));
        urlBuilder.append(String.format("%s%s=%s", urlBuilder.indexOf("?") > 0 ? "&" : "?", "random", random));
        return urlBuilder.toString();
    }

    /**
     * 注册任务
     * @param crawlerRequests 任务
     * @param removeDuplicated 是否移除相同任务
     * @return
     */
    public boolean regeditTaskToItemQueue(List<CrawlerRequest> crawlerRequests, boolean removeDuplicated) {
        try {
            List<String > queue = Lists.newArrayList();
            Map<String,String> crawlerRequestMap = Maps.newHashMap();
            logger.info("开始注册{}个任务", crawlerRequests.size());
            for (CrawlerRequest crawlerRequest : crawlerRequests) {
                Map<String, Object> parameter = crawlerRequest.getData();
                List<Object> arrayTmps = null;
                Map pagination = null;
                List datas = Lists.newArrayList();
                if (parameter == null || parameter.size() == 0) {
                    boolean isDuplicate = removeDuplicated ?false: isDuplicate(crawlerRequest);
                    if (isDuplicate) continue;
                    CrawlerRequest request = new CrawlerRequest();
                    BeanUtils.copyProperties(crawlerRequest, request);
                    String url = getRandomUrl(request);
                    String field = DigestUtils.sha1Hex(url);
                    request.setHashCode(field);
                    request.setUrl(url);
                    crawlerRequestMap.put(field,JSON.toJSONString(request));
                    queue.add(field);
                } else {
                    for (String key : parameter.keySet()) {
                        if (StringUtils.startsWith(key, "$array_")) {
                            arrayTmps = (List<Object>) parameter.get(key);
                            List<Map> tmpData = Lists.newArrayList();
                            String tmpParam = StringUtils.substringAfter(key, "$array_");
                            for (Object tmp : arrayTmps) {
                                tmpData.add(ImmutableMap.of(tmpParam, tmp));
                            }

                            datas.add(ImmutableSet.copyOf(tmpData));
                        } else if (StringUtils.startsWith(key, "$pagination_")) {
                            pagination =(Map)parameter.get(key);
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
                    if(datas!= null && datas.size() > 0) {
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
                            boolean isDuplicate = removeDuplicated ?false: isDuplicate(crawlerRequest);
                            if (isDuplicate) continue;
                            else {
                                String url = getRandomUrl(request);
                                String field = DigestUtils.sha1Hex(url);
                                request.setHashCode(field);
                                request.setUrl(url);
                                crawlerRequestMap.put(field,JSON.toJSONString(request));
                                queue.add(field);
                            }
                            if(queue.size() !=0 && queue.size() % taskCount == 0) {
                                regeditQueueMore(queue,crawlerRequestMap);
                                queue.clear();
                                crawlerRequestMap.clear();
                            }
                        }
                    }

                }

                if(queue.size() != 0 && queue.size() % taskCount == 0) {
                    regeditQueueMore(queue,crawlerRequestMap);
                }
            }
            if(queue.size() != 0 ) {
                regeditQueueMore(queue,crawlerRequestMap);
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * 批量注册任务
     * @param queueVal
     * @param crawlerRequestMap
     * @return
     */
    private boolean regeditQueueMore(List<String> queueVal, Map<String,String> crawlerRequestMap) {
        try {
            HashOperations hashOperations = redisTemplate.opsForHash();
            ListOperations listOperations = redisTemplate.opsForList();
            String key = getItemKey();
            String itemQueue = getItemQueueKey();
            listOperations.rightPushAll(itemQueue,queueVal);
            hashOperations.putAll(key,crawlerRequestMap);
            return true;
        } catch (Exception e) {
            logger.info("任务{}注册失败！错误：{}", JSON.toJSONString(queueVal),e.getMessage());
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

    /**
     * 从待执行队列中拿取指定数目的任务
     * @param taskCount 任务数目
     * @return
     */
    public List<CrawlerRequest> fetchTasksFromItemQueue(long taskCount) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        List<CrawlerRequest> crawlerRequests = new ArrayList<>();
        String queue = getItemQueueKey();
        String key = getItemKey();
        long size = listOperations.size(queue);
        if (size == 0)
            return crawlerRequests;
        List<String> queueStr = listOperations.range(queue, 0, taskCount - 1);
        for (String field : queueStr) {
            if (!hashOperations.hasKey(key, field))
                continue;
            String data = String.valueOf(hashOperations.get(key, field));
            CrawlerRequest crawlerRequest = JSON.parseObject(data, CrawlerRequest.class);
            crawlerRequests.add(crawlerRequest);
        }
        return crawlerRequests;
    }

    public void moveTaskToRunningQueue(WorkerHeartbeat workerHeartbeat, List<CrawlerRequest> crawlerRequests) {
        long startTime = System.currentTimeMillis();
        HashOperations hashOperations = redisTemplate.opsForHash();
        String runningQueue = getRunningKey();
        String key = getItemKey();
        String itemQueue = getItemQueueKey();
        String runningKey = getRunningQueueKey();
        ListOperations listOperations = redisTemplate.opsForList();
        for (CrawlerRequest crawlerRequest : crawlerRequests) {
            Map multimap = Maps.newHashMap();
            multimap.put("startTime",startTime);
            multimap.put("workId",workerHeartbeat.getWorkerId());
            crawlerRequest.setExtendMap(multimap);
            String field = crawlerRequest.getHashCode();
            String value = JSON.toJSONString(crawlerRequest);
            if (hashOperations.hasKey(runningQueue, field)) {
                listOperations.remove(runningKey,0,field);
            }
            listOperations.leftPush(runningKey, field);
            hashOperations.put(runningQueue, field, value);
            hashOperations.delete(key, field);
            listOperations.remove(itemQueue, 0, field);
        }
    }

    public void moveToFailQueue(String field, String message) {
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
        Map multimap = crawlerRequest.getExtendMap();
        multimap.put("startTime",startTime);
        multimap.put("message",message);
        crawlerRequest.setExtendMap(multimap);
        String value = JSON.toJSONString(crawlerRequest);
        if (hashOperations.hasKey(failQueue, field)) {
            listOperations.remove(failKey,0,field);
        }
        listOperations.leftPush(failKey, field);
        hashOperations.put(failQueue, field, value);

        hashOperations.delete(key, field);
        listOperations.remove(runningKey, 0, field);
    }

    public Object removeSuccessedTaskFromRunningQueue(CrawlerResult result) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        String runningQueue = getRunningKey();
        String runningKey = getRunningQueueKey();
        String field = result.getKey();
        if (!hashOperations.hasKey(runningQueue, field))
            return false;
        String data = String.valueOf(hashOperations.get(runningQueue, field));
        CrawlerRequest crawlerRequest = JSON.parseObject(data, CrawlerRequest.class);
        if(crawlerRequest==null) return false;
        Map multimap = crawlerRequest.getExtendMap();
        multimap.put("startTime",System.currentTimeMillis());
        multimap.put("result",result);
        crawlerRequest.setExtendMap(multimap);
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
        if (hashOperations.hasKey(successQueue, crawlerRequest.getHashCode())) {
            listOperations.remove(successKey,0,crawlerRequest.getHashCode());
        }
        listOperations.leftPush(successKey, crawlerRequest.getHashCode());
        hashOperations.put(successQueue, crawlerRequest.getHashCode(), JSON.toJSONString(crawlerRequest));

        return false;
    }

    /**
     * 刷新超时队列（把超时的运行中队列任务重新加入待执行队列）
     *
     * @param timeout
     */
    public void refreshBreakedQueue(Long timeout) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        SetOperations setOperations = redisTemplate.opsForSet();
        String key = getRunningKey();
        String uniqQueue = getSetKey();
        List<String> datas = hashOperations.values(key);
        List<String > queue = Lists.newArrayList();
        Map<String,String> crawlerRequestMap = Maps.newHashMap();
        for (String data : datas) {
            CrawlerRequest crawlerRequest = JSON.parseObject(data, CrawlerRequest.class);
            Map multimap = crawlerRequest.getExtendMap();
            long startTime = Long.parseLong(String.valueOf(multimap.get("startTime")));
            long oldTime = System.currentTimeMillis() - startTime;
            if (oldTime > timeout) {
                crawlerRequest.setExtendMap(null);
                setOperations.remove(uniqQueue, crawlerRequest.getHashCode());
                CrawlerRequest request = new CrawlerRequest();
                BeanUtils.copyProperties(crawlerRequest, request);
                String url = getRandomUrl(request);
                String field = DigestUtils.sha1Hex(url);
                request.setHashCode(field);
                request.setUrl(url);
                crawlerRequestMap.put(field,JSON.toJSONString(request));
                queue.add(field);
            }
            if(queue.size() != 0 && queue.size() % taskCount == 0) {
                regeditQueueMore(queue,crawlerRequestMap);
            }
        }
        if(queue.size() != 0) {
            regeditQueueMore(queue,crawlerRequestMap);
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
//        long size = listOperations.size(key);
//        long page = size - pageIndex * pageSize;
//        long offset = page - (pageSize - 1);
        int page = pageIndex * pageSize;
        int offset = page + (pageSize - 1);
        keys = listOperations.range(key, page, offset);
//        keys = listOperations.range(key,offset,page);
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
