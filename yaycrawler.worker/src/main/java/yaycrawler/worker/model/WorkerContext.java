package yaycrawler.worker.model;

import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
public class WorkerContext {
    public static final String workerId= UUID.randomUUID().toString();
    public static  String masterServerAddress;
    public static boolean isSuccessRegisted=false;
    public static WebApplicationContext webApplicationContext;



    public static String getContextPath()
    {
        if(webApplicationContext==null) throw new RuntimeException("WebApplicationContext 未设置值");
        return webApplicationContext.getServletContext().getContextPath();
    }

}
