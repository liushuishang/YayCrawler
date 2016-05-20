package yaycrawler.worker.model;

import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
public class WorkerContext {
    public static final String workerId= UUID.randomUUID().toString();
    public static boolean isSuccessRegisted=false;
    public static WebApplicationContext webApplicationContext;


    public static String getContextPath()
    {
        return webApplicationContext.getEnvironment().getProperty("context.path");
    }
    public static String getMasterServerAddress()
    {
        return webApplicationContext.getEnvironment().getProperty("master.server.address");
    }

    public static  long getHeartbeatInteval()
    {
        return Long.parseLong(webApplicationContext.getEnvironment().getProperty("worker.heartbeat.inteval"));
    }

    public static String getSignatureSecret()
    {
        return webApplicationContext.getEnvironment().getProperty("signature.token");
    }


}
