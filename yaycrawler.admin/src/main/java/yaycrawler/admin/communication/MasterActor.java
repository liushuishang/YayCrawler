package yaycrawler.admin.communication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import yaycrawler.common.model.CommunicationAPIs;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.model.TasksResult;
import yaycrawler.common.utils.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Component
public class MasterActor {
    @Value("${master.server.address}")
    private String masterServerAddress;
    @Value("${signature.token}")
    private String secret;

    /**
     * Admin向Master发送任务
     *
     * @return
     */
    public boolean publishTasks(CrawlerRequest... crawlerRequests) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_TASK_REGEDIT);
        RestFulResult result = HttpUtils.doSignedHttpExecute(secret, targetUrl, HttpMethod.POST, crawlerRequests);
        return result != null && !result.hasError();
    }

    /**
     * 查询worker的注册信息
     * @return
     */
    public Object retrievedWorkerRegistrations() {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_RETRIVED_WORKERS);
        RestFulResult result = HttpUtils.doSignedHttpExecute(secret, targetUrl, HttpMethod.POST, null);
        return result.getData();
    }

    public Object retrievedItemQueueRegistrations(TasksResult tasksResult) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_RETRIVED_ITEM_QUEUES);
        RestFulResult result = HttpUtils.doSignedHttpExecute(secret, targetUrl, HttpMethod.POST, tasksResult);
        return result.getData();
    }

    public Object retrievedSuccessQueueRegistrations(TasksResult tasksResult) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_RETRIVED_SUCCESS_QUEUES);
        RestFulResult result = HttpUtils.doSignedHttpExecute(secret, targetUrl, HttpMethod.POST, tasksResult);
        return result.getData();
    }

    public Object retrievedFailQueueRegistrations(TasksResult tasksResult) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_RETRIVED_FAIL_QUEUES);
        RestFulResult result = HttpUtils.doSignedHttpExecute(secret, targetUrl, HttpMethod.POST, tasksResult);
        return result.getData();
    }

    public Object retrievedRunningQueueRegistrations(TasksResult tasksResult) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_RETRIVED_RUNNING_QUEUES);
        RestFulResult result = HttpUtils.doSignedHttpExecute(secret, targetUrl, HttpMethod.POST, tasksResult);
        return result.getData();
    }
}
