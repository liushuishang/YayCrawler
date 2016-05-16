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
    public static final String WORKER_POST_MASTER_RESULT_NOTIFY = "/worker/crawlerResultNotify";

    public static final String MASTER_POST_WORKER_TASK_ASSIGN = "/master/assignTasks";


    public static String getFullRemoteUrl(String contextPath, String api) {
        if (!contextPath.endsWith("/")) contextPath += "/";
        if (api.startsWith("/")) api = api.substring(1, api.length());
        return contextPath + api;
    }
}
