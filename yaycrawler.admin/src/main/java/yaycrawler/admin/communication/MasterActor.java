package yaycrawler.admin.communication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import yaycrawler.common.model.CommunicationAPIs;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.utils.HttpUtils;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Component
public class MasterActor {
    @Value("${master.server.address}")
    private String masterServerAddress;

    @Value("${context.path}")
    private String contextPath;

    /**
     * Admin想Master发送任务
     *
     * @return
     */

    public boolean regeditTasks(List<CrawlerRequest> crawlerRequests) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.ADMIN_POST_MASTER_TASK_REGEDIT);
        RestFulResult result = HttpUtils.doHttpExecute(targetUrl, HttpMethod.POST, crawlerRequests);
        return result != null && !result.hasError();
    }
}
