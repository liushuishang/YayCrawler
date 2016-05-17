package yaycrawler.common.model;

/**
 * 记录各个组件间通信的http协议接口
 * Created by ucs_yuananyun on 2016/5/13.
 */
public class CommunicationAPIs {

    /**
     * worker向master注册
     */
    public static final String WORKER_POST_MASTER_REGISTER = "/worker/register";
    public static final String WORKER_POST_MASTER_HEARTBEAT = "/worker/heartBeat";
    public static final String WORKER_POST_MASTER_SUCCESS_NOTIFY = "/worker/crawlerSuccessNotify";
    public static final String WORKER_POST_MASTER_FAILURE_NOTIFY="/worker/crawlerFailureNotify";

    public static final String MASTER_POST_WORKER_TASK_ASSIGN = "/master/assignTasks";

    public static final String ADMIN_POST_MASTER_TASK_REGEDIT = "/admin/registerQueues";
    public static String ADMIN_POST_MASTER_RETRIVED_WORKERS="/admin/retrievedWorkerRegistrations";

    public static String getFullRemoteUrl(String contextPath, String api) {
        if (!contextPath.endsWith("/")) contextPath += "/";
        if (api.startsWith("/")) api = api.substring(1, api.length());
        return contextPath + api;
    }
}
